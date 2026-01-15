package org.padan.Model.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jdk.jfr.Description;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.padan.Model.KafkaSetup.KafkaRentalConsumer;
import org.padan.Model.KafkaSetup.KafkaRentalTopic;
import org.padan.Model.Manager.ReservationManager;
import org.padan.Model.Objects.Reservation;
import org.padan.Model.Objects.Room;
import org.padan.Model.Objects.StudentUser;
import org.padan.Model.Objects.User;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.padan.Model.Objects.RoomType.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KafkaTest {

    private ReservationManager reservationManager;
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private MongoClient mongoClient;

    private ExecutorService executorService;
    private KafkaRentalConsumer consumer1;
    private KafkaRentalConsumer consumer2;

    private Room testRoom1;
    private Room testRoom2;
    private Room testRoom3;
    private User testUser;
    private Reservation testRes;

    @BeforeEach
    public void setup() throws InterruptedException {
        new KafkaRentalTopic().createTopic();
        reservationRepository = new ReservationRepository();
        roomRepository = new RoomRepository();
        userRepository = new UserRepository();

        this.mongoClient = reservationRepository.getMongoClient();
        reservationManager = new ReservationManager(reservationRepository, mongoClient);

        String connectionString = "mongodb://admin:adminpassword@localhost:27017,localhost:27018,localhost:27019/?authSource=admin&replicaSet=replica_set_single";
        try (MongoClient setupClient = MongoClients.create(connectionString)) {
            setupClient.getDatabase("rentafield").getCollection("reservations").drop();
        }

        String id = UUID.randomUUID().toString();

        executorService = Executors.newFixedThreadPool(2);
        consumer1 = new KafkaRentalConsumer(id);
        consumer2 = new KafkaRentalConsumer(id);

        executorService.submit(consumer1);
        executorService.submit(consumer2);

        testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(new ObjectId());

        testRoom1 = Room.builder()
                .roomId(new ObjectId())
                .basePrice(12d)
                .roomType(COURT)
                .capacity(20)
                .build();

        testRoom2 = Room.builder()
                .roomId(new ObjectId())
                .basePrice(12d)
                .roomType(HALL)
                .capacity(20)
                .build();

        testRoom3 = Room.builder()
                .roomId(new ObjectId())
                .basePrice(12d)
                .roomType(GYM)
                .capacity(20)
                .build();
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        if (consumer1 != null) {
            consumer1.shutdown();
        }
        if (consumer2 != null) {
            consumer2.shutdown();
        }

        if (executorService != null) {
            executorService.shutdown();
        }

        reservationRepository.close();
        roomRepository.close();
        userRepository.close();
        mongoClient.close();
    }

    @Test
    @Order(1)
    public void makeReservationWithKafka_1() throws InterruptedException {

        testRes = Reservation.builder()
                .reservationId(new ObjectId())
                .room(testRoom1)
                .user(testUser)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        reservationManager.makeReservation(testRes);

        MongoCollection<Document> collection = this.mongoClient.getDatabase("rentafield").getCollection("reservations");

        Document searchedDocument = null;

        for (int i = 0; i < 50; i++) {
            searchedDocument = collection.find(Filters.eq("_id", testRes.getReservationId().toString())).first();

            if (searchedDocument != null) {
                break;
            }

            Thread.sleep(300);
        }

        assertNotNull(searchedDocument);
        assertEquals("Padan Rental", searchedDocument.getString("rentalName"));

        System.out.println("\n\n\n\nRezerwacja w kafce:");
        System.out.println(searchedDocument.toJson().toString());
        System.out.println("\n\n\n\n");
    }

    @Test
    @Order(2)
    @Description("Kilka rezerwacji w kafce")
    public void makeReservationWithKafka_2() throws InterruptedException {
        Reservation firstRes = Reservation.builder()
                .reservationId(new ObjectId())
                .room(testRoom2)
                .user(testUser)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Reservation secondRes = Reservation.builder()
                .reservationId(new ObjectId())
                .room(testRoom3)
                .user(testUser)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        reservationManager.makeReservation(firstRes);
        reservationManager.makeReservation(secondRes);

        MongoCollection<Document> collection = this.mongoClient.getDatabase("rentafield").getCollection("reservations");

        Document searchedDocument1 = null;
        Document searchedDocument2 = null;

        for (int i = 0; i < 50; i++) {
            searchedDocument1 = collection.find(Filters.eq("_id", firstRes.getReservationId().toString())).first();
            searchedDocument2 = collection.find(Filters.eq("_id", secondRes.getReservationId().toString())).first();

            if (searchedDocument1 != null || searchedDocument2 != null) {
                break;
            }

            Thread.sleep(300);
        }

        assertNotNull(searchedDocument1);
        assertNotNull(searchedDocument2);
        assertEquals("Padan Rental", searchedDocument1.getString("rentalName"));
        assertEquals("Padan Rental", searchedDocument2.getString("rentalName"));

        System.out.println("\n\n\n\nRezerwacja w kafce:");
        System.out.println(searchedDocument1.toJson().toString());
        System.out.println(searchedDocument2.toJson().toString());
        System.out.println("\n\n\n\n");
    }
}