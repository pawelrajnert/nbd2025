package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Manager.ReservationManager;
import org.padan.Model.Objects.*;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.padan.Model.Objects.RoomType.COURT;

class ReservationRepositoryTest {
    private ReservationRepository repository;
    private ClientSession session;
    private ReservationManager reservationManager;

    private Room testRoom;
    private User testUser;
    private Reservation testReservation;
    private ObjectId testId;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        repository = new ReservationRepository();
        reservationManager = new ReservationManager(repository, repository.getMongoClient());
        session = repository.startSession();

        try {
            for (Reservation reservation : repository.findAll()) {
                reservationManager.removeReservation(reservation.getReservationId());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        testId = new ObjectId("000000000000000000000000");

        testStartTime = LocalDateTime.of(2025, 10, 11, 15, 0);
        testEndTime = LocalDateTime.of(2025, 10, 11, 17, 0);

        testRoom = Room.builder()
                .roomId(testId)
                .basePrice(12d)
                .roomType(COURT)
                .capacity(20)
                .build();

        testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(testId);

        testReservation = Reservation.builder()
                .reservationId(testId)
                .room(testRoom)
                .user(testUser)
                .startTime(testStartTime)
                .endTime(testEndTime)
                .build();
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            try {
                reservationManager.removeReservation(testId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                session.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (repository != null) {
            try {
                for (Reservation reservation : repository.findAll()) {
                    reservationManager.removeReservation(reservation.getReservationId());
                }
                repository.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    void addReservation() {
        ObjectId anId = new ObjectId("000000000000000000000001");
        Reservation res;

        LocalDateTime start = LocalDateTime.of(2025, 10, 10, 15, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 10, 17, 0);

        res = Reservation.builder()
                .reservationId(anId)
                .room(testRoom)
                .user(testUser)
                .startTime(start)
                .endTime(end)
                .build();

        assertEquals(0, repository.findAll().size());

        reservationManager.makeReservation(res);

        assertEquals(1, repository.findAll().size());

        reservationManager.makeReservation(testReservation);

        assertEquals(2, repository.findAll().size());

        assertEquals(res.getReservationId(), repository.findById(anId).getReservationId());
        assertEquals(res.getStartTime(), repository.findById(anId).getStartTime());
        assertEquals(res.getEndTime(), repository.findById(anId).getEndTime());
        assertEquals(res.getUser().getFirstName(), repository.findById(anId).getUser().getFirstName());
        assertEquals(res.getUser().getLastName(), repository.findById(anId).getUser().getLastName());
        assertEquals(res.getUser().getEmail(), repository.findById(anId).getUser().getEmail());
    }

    @Test
    void updateReservation() {
        reservationManager.makeReservation(testReservation);
        assertEquals(testReservation.getUser().getFirstName(), repository.findById(testReservation.getReservationId()).getUser().getFirstName());

        testReservation.setUser(new StudentUser("Testv2", "Studentv2", "tsv2@gmail.com"));
        LocalDateTime st = LocalDateTime.of(2025, 10, 12, 15, 0);
        LocalDateTime et = LocalDateTime.of(2025, 10, 12, 17, 0);
        testReservation.setStartTime(st);
        testReservation.setEndTime(et);

        System.out.println("\n\n\n");
        System.out.println(testReservation.getUser().toString());
        System.out.println("\n\n\n");

        reservationManager.updateReservation(testReservation.getReservationId(), testReservation);

        assertEquals(testReservation.getUser().getFirstName(), repository.findById(testReservation.getReservationId()).getUser().getFirstName());
        assertEquals(testReservation.getUser().getLastName(), repository.findById(testReservation.getReservationId()).getUser().getLastName());
        assertEquals(testReservation.getUser().getEmail(), repository.findById(testReservation.getReservationId()).getUser().getEmail());
    }

    @Test
    void checkExistingReservation() {
        reservationManager.makeReservation(testReservation);

        System.out.println("\n\n\n");
        System.out.println(testReservation.toString());
        System.out.println("\n\n\n");

        assertEquals(testReservation.getUser().getFirstName(), repository.findById(testReservation.getReservationId()).getUser().getFirstName());
        assertEquals(testReservation.getUser().getLastName(), repository.findById(testReservation.getReservationId()).getUser().getLastName());
        assertEquals(testReservation.getUser().getEmail(), repository.findById(testReservation.getReservationId()).getUser().getEmail());
        assertEquals(testReservation.getStartTime(), repository.findById(testReservation.getReservationId()).getStartTime());
        assertEquals(testReservation.getEndTime(), repository.findById(testReservation.getReservationId()).getEndTime());
    }

    @Test
    void deleteReservation() {
        assertEquals(repository.findAll().size(), 0);

        reservationManager.makeReservation(testReservation);
        System.out.println("\n\n\n");
        System.out.println(testReservation.toString());
        System.out.println("\n\n\n");

        assertEquals(repository.findAll().size(), 1);

        reservationManager.removeReservation(testReservation.getReservationId());

        assertEquals(repository.findAll().size(), 0);
    }

    @Test
    void fromPojoTest() {
        reservationManager.makeReservation(testReservation);

        MongoCollection<Document> raw = repository.getRentAFieldDB().getCollection("reservations", Document.class);
        Document doc = raw.find(eq("_id", testId)).first();

        assertNotNull(doc);
        assertTrue(doc.containsKey("start_time"));
        assertTrue(doc.containsKey("room"));
        assertTrue(doc.containsKey("user"));

        Document roomDoc = doc.get("room", Document.class);
        assertEquals("COURT", roomDoc.getString("room_type"));

        Document userDoc = doc.get("user", Document.class);
        assertEquals("ts@gmail.com", userDoc.getString("email"));
    }

    @Test
    void toPojoTest() {
        reservationManager.makeReservation(testReservation);

        Reservation pom = repository.findById(testId);
        assertNotNull(pom);
        assertEquals(testReservation.getStartTime(), pom.getStartTime());
        assertEquals(testReservation.getUser().getEmail(), pom.getUser().getEmail());
        assertEquals(testReservation.getRoom().getRoomType(), pom.getRoom().getRoomType());
    }

    @Test
    void concurrentReservationTest() {
        reservationManager.makeReservation(testReservation);

        System.out.println("\n\n\n");
        System.out.println(testReservation.getStartTime());
        System.out.println(testReservation.getEndTime());
        System.out.println("\n\n\n");

        ObjectId v2Id = new ObjectId("000000000000000111000001");
        Reservation concurrentRes;

        concurrentRes = Reservation.builder()
                .reservationId(v2Id)
                .room(testRoom)
                .user(testUser)
                .startTime(testStartTime)
                .endTime(testEndTime)
                .build();

        assertEquals(1, repository.findAll().size());
        assertThrows(RuntimeException.class, () -> reservationManager.makeReservation(concurrentRes));
    }
}
