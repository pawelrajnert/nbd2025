package org.padan.Model.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.BaseTC;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest extends BaseTC {

    private User user1;
    private User user2;
    private Room room1;
    private Room room2;
    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    public void createExampleReservation() {
        user1 = new StudentUser("Student", "Student", "student@gmail.com");
        user2 = new RegularUser("Regular", "Regular", "regular@gmail.com", 3);
        um.registerUser(user1);
        um.registerUser(user2);

        room1 = new Room(RoomType.GYM, 50, 100.);
        room2 = new Room(RoomType.HALL, 20, 250.);
        rm.addRoom(room1);
        rm.addRoom(room2);

        LocalDateTime start1 = LocalDateTime.of(2025, 10, 10, 15, 0);
        LocalDateTime end1 = LocalDateTime.of(2025, 10, 10, 17, 0);
        reservation1 = new Reservation(room1, user1, start1, end1, room1.getBasePrice());

        LocalDateTime start2 = LocalDateTime.of(2025, 10, 20, 12, 0);
        LocalDateTime end2 = LocalDateTime.of(2025, 10, 20, 14, 0);
        reservation2 = new Reservation(room2, user2, start2, end2, room2.getBasePrice());

        resm.makeReservation(reservation1);
        resm.makeReservation(reservation2);
    }

    @Test
    void testReservationData() {
        assertEquals(room1, reservation1.getRoom());
        assertEquals(user1, reservation1.getUser());
        assertEquals(room1.getBasePrice(), reservation1.getRoom().getBasePrice());
        assertEquals(LocalDateTime.of(2025, 10, 10, 15, 0), reservation1.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 10, 17, 0), reservation1.getEndTime());

        assertEquals(room2, reservation2.getRoom());
        assertEquals(user2, reservation2.getUser());
        assertEquals(room2.getBasePrice(), reservation2.getRoom().getBasePrice());
        assertEquals(LocalDateTime.of(2025, 10, 20, 12, 0), reservation2.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 20, 14, 0), reservation2.getEndTime());
    }

    @Test
    public void changeToValidData() {
        reservation1.setRoom(room2);
        reservation1.setUser(user2);
        LocalDateTime newStart = LocalDateTime.of(2025, 10, 11, 16, 0);
        LocalDateTime newEnd = LocalDateTime.of(2025, 10, 11, 17, 45);

        resm.updateReservation(reservation1, reservation1.getReservationId(), newStart, newEnd);

        assertEquals(room2, reservation1.getRoom());
        assertEquals(user2, reservation1.getUser());
        assertEquals(room2.getBasePrice(), reservation1.getRoom().getBasePrice());
        assertEquals(newStart, reservation1.getStartTime());
        assertEquals(newEnd, reservation1.getEndTime());
    }

    @Test
    public void sameRoomRental() {
        int versionHolder = reservation1.getVersion();
        reservation1.setRoom(room2);

        LocalDateTime newStart = reservation2.getStartTime();
        LocalDateTime newEnd = reservation2.getEndTime();

        resm.updateReservation(reservation1, reservation1.getReservationId(), newStart, newEnd);

        // jeśli nie zmieni się wersja oznacza to, że nie zmieniono nic w bazie danych
        assertEquals(versionHolder, reservation1.getVersion());
    }

    @Test
    public void deleteAllReservations() {
        resm.cancelReservation(reservation1.getReservationId());
        resm.cancelReservation(reservation2.getReservationId());

        assertNull(resm.findReservation(reservation1.getReservationId()));
        assertNull(resm.findReservation(reservation2.getReservationId()));
    }

    @Test
    public void sameReservationTest(){
        resm.cancelReservation(reservation2.getReservationId());
        Reservation newReservation = new Reservation(room1, user2, reservation1.getStartTime(), reservation1.getEndTime(), room1.getBasePrice());
        resm.makeReservation(newReservation);
        assertThrows(NullPointerException.class, () -> resm.findReservation(newReservation.getReservationId()));
    }
}