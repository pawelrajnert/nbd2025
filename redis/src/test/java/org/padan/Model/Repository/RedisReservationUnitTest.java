package org.padan.Model.Repository;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Objects.*;
import org.padan.Model.RedisConnect;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.padan.Model.Objects.RoomType.COURT;
import static org.padan.Model.Objects.RoomType.HALL;

public class RedisReservationUnitTest {
    private static RedisReservation reservation;
    private Room testRoom;
    private User testUser;
    private Reservation testReservation;
    private LocalDateTime start;
    private LocalDateTime end;
    private ObjectId id;

    @AfterAll
    public static void tearDown() {
        try {
            RedisConnect.startConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void init() {
        reservation = new RedisReservation(RedisConnect.startConnection());

        id = new ObjectId("000000000000000000000000");

        testRoom = Room.builder()
                .roomId(id)
                .basePrice(12d)
                .roomType(COURT)
                .capacity(20)
                .build();

        testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(id);

        start = LocalDateTime.of(2025, 10, 11, 15, 00);
        end = LocalDateTime.of(2025, 10, 11, 17, 00);

        testReservation = Reservation.builder()
                .reservationId(id)
                .room(testRoom)
                .user(testUser)
                .startTime(start)
                .endTime(end)
                .build();

        try {
            RedisConnect.startConnection().flushAll();
        } catch (Exception e) {
        }
    }

    @Test
    void createReservation() {
        reservation.putOneInCache(id, testReservation, 500);

        Optional<Reservation> res = reservation.findById(id);
        assertTrue(res.isPresent());

        Reservation reader = res.get();
        assertEquals(testReservation.getReservationId(), reader.getReservationId());
        assertEquals(testReservation.getRoom(), reader.getRoom());
        assertEquals(testReservation.getUser(), reader.getUser());
        assertEquals(testReservation.getStartTime(), reader.getStartTime());
        assertEquals(testReservation.getEndTime(), reader.getEndTime());
    }

    @Test
    void putOneinCacheAndDelete() {
        reservation.putOneInCache(id, testReservation, 500);
        Optional<Reservation> res = reservation.findById(id);

        assertTrue(res.isPresent());
        Reservation reader = res.get();
        assertEquals(testReservation.getReservationId(), reader.getReservationId());
        assertEquals(testReservation.getRoom(), reader.getRoom());
        assertEquals(testReservation.getUser(), reader.getUser());
        assertEquals(testReservation.getStartTime(), reader.getStartTime());
        assertEquals(testReservation.getEndTime(), reader.getEndTime());

        reservation.deleteOneInCache(id);

        Optional<Reservation> res2 = reservation.findById(id);
        assertTrue(res2.isEmpty());
    }

    @Test
    void putAllInCacheAndDelete() {
        ObjectId newID = new ObjectId("000000000000000000000001");
        Room newRoom = Room.builder()
                .roomId(newID)
                .basePrice(20d)
                .roomType(HALL)
                .capacity(20)
                .build();

        TrainerUser newUser = new TrainerUser("TestNew", "Trainer", "tu@gmail.com", true);
        newUser.setUserId(newID);

        LocalDateTime newStart = LocalDateTime.of(2025, 10, 12, 20, 0);
        LocalDateTime newEnd = LocalDateTime.of(2025, 10, 12, 22, 0);

        Reservation newReservation = Reservation.builder()
                .reservationId(newID)
                .room(newRoom)
                .user(newUser)
                .startTime(newStart)
                .endTime(newEnd)
                .build();

        List<Reservation> list = List.of(testReservation, newReservation);

        reservation.putAllInCache(list, 500);

        Optional<List<Reservation>> cached = reservation.getAll();
        assertTrue(cached.isPresent());
        assertEquals(2, cached.get().size());

        reservation.deleteAll();

        Optional<List<Reservation>> afterDelete = reservation.getAll();
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void findOneInCache() {
        reservation.putOneInCache(id, testReservation, 500);

        Optional<Reservation> res = reservation.findById(id);
        assertTrue(res.isPresent());
        assertEquals(1, res.stream().count());
    }

    @Test
    void getAllInCache() {
        ObjectId newID = new ObjectId("000000000000000000000001");
        Room newRoom = Room.builder()
                .roomId(newID)
                .basePrice(20d)
                .roomType(HALL)
                .capacity(20)
                .build();

        TrainerUser newUser = new TrainerUser("TestNew", "Trainer", "tu@gmail.com", true);
        newUser.setUserId(newID);

        LocalDateTime newStart = LocalDateTime.of(2025, 10, 12, 20, 0);
        LocalDateTime newEnd = LocalDateTime.of(2025, 10, 12, 22, 0);

        Reservation newReservation = Reservation.builder()
                .reservationId(newID)
                .room(newRoom)
                .user(newUser)
                .startTime(newStart)
                .endTime(newEnd)
                .build();

        reservation.putAllInCache(List.of(testReservation, newReservation), 500);
        Optional<List<Reservation>> cached = reservation.getAll();
        assertTrue(cached.isPresent());
        assertEquals(2, cached.get().size());
    }

}
