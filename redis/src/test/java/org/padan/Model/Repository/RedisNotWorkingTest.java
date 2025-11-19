package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.padan.Model.Objects.*;
import org.padan.Model.RedisConnect;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.padan.Model.Objects.RoomType.COURT;
import static org.padan.Model.Objects.RoomType.GYM;


class RedisNotWorkingTest {
    private RedisRepository redisRepository;
    private ReservationRepository mongoRepository;
    private Reservation testReservation;
    private ObjectId testId;
    private User testUser;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        mongoRepository = new ReservationRepository();

        try {
            Jedis jedis = RedisConnect.startConnection();
            jedis.flushAll();
            RedisReservation redisCache = new RedisReservation(jedis);
            redisRepository = new RedisRepository(mongoRepository, redisCache);
        } catch (Exception e) {
            e.printStackTrace();
            RedisReservation redisCache = new RedisReservation(RedisConnect.startConnection());
            redisRepository = new RedisRepository(mongoRepository, redisCache);
        }

        testId = new ObjectId("000000000000000000000000");

        testRoom = Room.builder()
                .roomId(testId)
                .basePrice(100.0)
                .roomType(COURT)
                .capacity(10)
                .build();

        testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(testId);

        testReservation = Reservation.builder()
                .reservationId(testId)
                .room(testRoom)
                .user(testUser)
                .startTime(LocalDateTime.of(2025, 12, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 12, 1, 12, 0))
                .build();
    }

    @AfterEach
    void tearDown() {
        ClientSession session = mongoRepository.startSession();
        try {
            mongoRepository.remove(session, testId);
        } catch (Exception e) {
        } finally {
            session.close();
        }

        try {
            RedisConnect.startConnection().flushAll();
        } catch (Exception e) {
        }
    }

    @Test
    void testFindById() {
        ClientSession session = mongoRepository.startSession();
        try {
            redisRepository.add(session, testReservation);
        } finally {
            session.close();
        }
        Reservation added = redisRepository.findById(testId);

        assertNotNull(added);
        assertEquals(testId, added.getReservationId());
        assertEquals(testReservation, added);
    }

    @Test
    void testFindAll() {
        ClientSession session = mongoRepository.startSession();
        try {
            mongoRepository.add(session, testReservation);
        } finally {
            session.close();
        }

        List<Reservation> results = redisRepository.findAll();

        assertNotNull(results);
        assertTrue(results.size() >= 1);
    }

    @Test
    void testAdd() {
        ClientSession session = mongoRepository.startSession();

        try {
            redisRepository.add(session, testReservation);

            Reservation added = mongoRepository.findById(testId);
            assertNotNull(added);
            assertEquals(testId, added.getReservationId());

        } finally {
            session.close();
        }
    }

    @Test
    void testUpdate() {
        ClientSession session = mongoRepository.startSession();
        try {
            mongoRepository.add(session, testReservation);
        } finally {
            session.close();
        }

        testRoom.setRoomType(GYM);

        Reservation newRes = Reservation.builder()
                .reservationId(testId)
                .room(testReservation.getRoom())
                .user(testReservation.getUser())
                .startTime(LocalDateTime.of(2025, 12, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 12, 1, 16, 0))
                .build();

        session = mongoRepository.startSession();
        try {
            redisRepository.update(session, testId, newRes);
        } finally {
            session.close();
        }

        Reservation found = mongoRepository.findById(testId);
        assertNotNull(found);
        assertEquals(RoomType.GYM, found.getRoom().getRoomType());
    }

    @Test
    void testRemove() {
        ClientSession session = mongoRepository.startSession();
        try {
            mongoRepository.add(session, testReservation);
        } finally {
            session.close();
        }

        session = mongoRepository.startSession();
        try {
            redisRepository.remove(session, testId);
        } finally {
            session.close();
        }

        Reservation added = mongoRepository.findById(testId);
        assertNull(added);
    }
}