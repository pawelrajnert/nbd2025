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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                            "roomType": "COURT",
                            "capacity": 2,
                            "basePrice": 120.0
                          },
                          "user": {
                            "clazz": "student",
                            "userId": "de4a9651-f046-42b3-9a3d-d0c9753dfbf5",
                            "firstName": "Aaa",
                            "lastName": "BBB",
                            "email": "aaabbb@example.com",
                          },
                          "startTime": { "$date": "2025-11-02T14:00:00Z" },
                          "endTime": { "$date": "2025-11-05T10:00:00Z" },
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
}
