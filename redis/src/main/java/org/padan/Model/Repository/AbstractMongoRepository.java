package org.padan.Model.Repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.padan.Model.Codecs.UserCodecProvider;
import org.padan.Model.Objects.*;

import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

@Getter
public abstract class AbstractMongoRepository implements AutoCloseable {
    private ConnectionString connectionString = new ConnectionString(
            "mongodb://admin:adminpassword@localhost:27017/?authSource=admin");

    private MongoCredential credential = MongoCredential.createCredential(
            "admin", "admin", "adminpassword".toCharArray());

    private CodecRegistry pojoCodecRegistry = fromProviders(
            PojoCodecProvider.builder()
                    .automatic(true)
                    .register(User.class, StudentUser.class, RegularUser.class, TrainerUser.class,
                            Reservation.class, Room.class)
                    .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
                    .build());

    private MongoClient mongoClient;
    private MongoDatabase rentAFieldDB;

    private void initDbConnection() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        fromProviders(new UserCodecProvider()),
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                )).build();

        mongoClient = MongoClients.create(settings);
        rentAFieldDB = mongoClient.getDatabase("rentafield");
    }

    public AbstractMongoRepository() {
        initDbConnection();
    }

    public ClientSession startSession() {
        return mongoClient.startSession();
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
