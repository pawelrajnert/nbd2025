package org.padan.Model.Repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MongoDBConnectionTest {
    private static ConnectionString connectionString = new ConnectionString(
            "mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_single");

    private static MongoCredential credential = MongoCredential.createCredential(
            "admin", "admin", "adminpassword".toCharArray());

    private static CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder()
                    .automatic(true)
                    .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
                    .build());

    private static MongoClient mongoClient;
    private static MongoDatabase rentAFieldDB;

    private static void initDbConnection() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.STANDARD)),
                        pojoCodecRegistry
                )).build();

        mongoClient = MongoClients.create(settings);
        rentAFieldDB = mongoClient.getDatabase("rentafield");
    }

    @BeforeAll
    static void setUp() {
        initDbConnection();
    }

    @Test
    void testInsert() {
        MongoCollection<Document> collection = rentAFieldDB.getCollection("test");
        assertDoesNotThrow(() ->
                collection.insertOne(Document.parse("""
                        {
                          "_id": { "$oid": "671f3c6f2b6a4a0012ab1234" },
                          "room": {
                            "roomId": "dc8f5c92-5dbb-45cc-8b7a-2af1cb28f5e9",
                            "room_type": "COURT",
                            "capacity": 2,
                            "base_price": 120.0
                          },
                          "user": {
                            "clazz": "student",
                            "userId": "de4a9651-f046-42b3-9a3d-d0c9753dfbf5",
                            "first_name": "Aaa",
                            "last_name": "BBB",
                            "email": "aaabbb@example.com",
                          },
                          "start_time": { "$date": "2025-11-02T14:00:00Z" },
                          "end_time": { "$date": "2025-11-05T10:00:00Z" },
                          "price": 360.0
                        }
                        """)));
        collection.deleteOne(Filters.eq("_id", new ObjectId("671f3c6f2b6a4a0012ab1234")));
    }

    @Test
    void testPing() {
        assertDoesNotThrow(() -> {
            rentAFieldDB.runCommand(new Document("ping", 1));
        });
    }

    @Test
    void testPrimaryFailover() throws IOException, InterruptedException {
        assertDoesNotThrow(() -> rentAFieldDB.runCommand(new Document("ping", 1)));
        new ProcessBuilder("docker", "stop", "mongodb1").inheritIO().start().waitFor();

        boolean reconnected = false;
        Instant start = Instant.now();

        while (Duration.between(start, Instant.now()).toSeconds() < 60) {
            try {
                rentAFieldDB.runCommand(new Document("ping", 1));
                reconnected = true;
                break;
            } catch (Exception e) {
                Thread.sleep(3000);
            }
        }
        assertTrue(reconnected, "Driver should reconnect after PRIMARY failover");

        new ProcessBuilder("docker", "start", "mongodb1").inheritIO().start().waitFor();
    }

    @Test
    void changingServersTest() throws IOException, InterruptedException {
        MongoCollection<Document> collection = rentAFieldDB.getCollection("test");

        new ProcessBuilder("docker", "start", "mongodb1").inheritIO().start().waitFor();
        new ProcessBuilder("docker", "start", "mongodb2").inheritIO().start().waitFor();
        new ProcessBuilder("docker", "start", "mongodb3").inheritIO().start().waitFor();
        printServerStatus();

        ObjectId id1 = new ObjectId("700000000000000111000001");
        ObjectId id2 = new ObjectId("700000000000000111000002");
        ObjectId id3 = new ObjectId("700000000000000111000003");

        Document doc1 = new Document("_id", id1)
                .append("first_name", "Aaa")
                .append("last_name", "Bbb")
                .append("email", "aaabbb@gmail.com");

        Document doc2 = new Document("_id", id2)
                .append("first_name", "Ccc")
                .append("last_name", "Ddd")
                .append("email", "cccddd@gmail.com");

        Document doc3 = new Document("_id", id3)
                .append("first_name", "Eee")
                .append("last_name", "Fff")
                .append("email", "eeefff@gmail.com");

        collection.deleteOne(Filters.eq("_id", id1));
        collection.deleteOne(Filters.eq("_id", id2));
        collection.deleteOne(Filters.eq("_id", id3));

        assertDoesNotThrow(() -> collection.insertOne(doc1));
        assertNotNull(collection.find(Filters.eq("_id", id1)).first());

        new ProcessBuilder("docker", "stop", "mongodb1").inheritIO().start().waitFor();
        printServerStatus();

        assertDoesNotThrow(() -> collection.insertOne(doc2));
        assertNotNull(collection.find(Filters.eq("_id", id2)).first());

        new ProcessBuilder("docker", "stop", "mongodb2").inheritIO().start().waitFor();
        printServerStatus();

        assertThrows(com.mongodb.MongoTimeoutException.class, () -> collection.insertOne(doc3));

        new ProcessBuilder("docker", "start", "mongodb1").inheritIO().start().waitFor();
        new ProcessBuilder("docker", "start", "mongodb2").inheritIO().start().waitFor();
        printServerStatus();

        assertDoesNotThrow(() -> collection.insertOne(doc3));
        assertNotNull(collection.find(Filters.eq("_id", id1)).first());
        assertNotNull(collection.find(Filters.eq("_id", id2)).first());
        assertNotNull(collection.find(Filters.eq("_id", id3)).first());

        collection.deleteOne(Filters.eq("_id", id1));
        collection.deleteOne(Filters.eq("_id", id2));
        collection.deleteOne(Filters.eq("_id", id3));
    }


    private void printServerStatus() {
        {
            List<String> hosts = List.of("mongodb1:27017", "mongodb2:27018", "mongodb3:27019");

            for (String host : hosts) {
                try (MongoClient client = MongoClients.create("mongodb://" + host + "/?replicaSet=replica_set_single")) {
                    Document result = client.getDatabase("admin").runCommand(new Document("isMaster", 1));
                    boolean isPrimary = result.getBoolean("ismaster", false);
                    boolean isSecondary = result.getBoolean("secondary", false);
                    System.out.println("\n\n\nHostname:" + host);
                    System.out.println("isPrimary:" + isPrimary);
                    System.out.println("isSecondary:" + isSecondary + "\n\n\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("\n\n\n");
        }
    }
}
