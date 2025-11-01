package org.padan.Model.Repository;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Objects.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.padan.Model.Objects.RoomType.COURT;

class ReservationRepositoryTest {

    ReservationRepository repository;

    Room testRoom;
    User testUser;
    Reservation testReservation;
    ObjectId testId;
    LocalDateTime testStartTime;
    LocalDateTime testEndTime;

    @BeforeEach
    void setUp() {
        testId = new ObjectId("000000000000000000000000");
        repository = new ReservationRepository();
        repository.remove(testId);
        testStartTime = LocalDateTime.now();
        testEndTime = LocalDateTime.now().plusHours(2);
        repository.remove(testId);
        testRoom = Room.builder()
                .roomId(testId)
                .basePrice(12d)
                .roomType(RoomType.COURT)
                .capacity(20)
                .build();
        testUser = new StudentUser("a", "b", "asd");
        testUser.setUserId(testId);
        testReservation = Reservation.builder()
                .reservationId(testId)
                .room(testRoom)
                .user(testUser)
                .startTime(testStartTime)
                .endTime(testEndTime)
                .build();
        Repository<Reservation> reservationRepository = new ReservationRepository();
    }

    @AfterEach
    void tearDown() {
        repository.remove(testId);
    }

    @Test
    void testAddFind() {
        int countBefore = repository.findAll().size();

        assertDoesNotThrow(()->repository.add(testReservation));
        assertTrue(countBefore < repository.findAll().size());
        assertFalse(repository.findAll().isEmpty());
        assertEquals(testEndTime.truncatedTo(ChronoUnit.MILLIS),
                repository.findById(testId).getEndTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(testStartTime.truncatedTo(ChronoUnit.MILLIS),
                repository.findById(testId).getStartTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(testRoom, repository.findById(testId).getRoom());
        assertEquals(testUser, repository.findById(testId).getUser());
    }


    @Test
    void testUpdate(){
        LocalDateTime testTime = LocalDateTime.now().plusHours(10);
        repository.add(testReservation);
        Reservation reservation = repository.findById(testId);
        reservation.setEndTime(testTime);
        repository.update(testId, reservation);
        assertEquals(testTime.truncatedTo(ChronoUnit.MILLIS),
                repository.findById(testId).getEndTime().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void testRemove() {
        repository.add(testReservation);
        int countBefore = repository.findAll().size();
        repository.remove(testId);
        assertTrue(countBefore > repository.findAll().size());
    }
}