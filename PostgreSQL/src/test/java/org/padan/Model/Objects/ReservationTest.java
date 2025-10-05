package org.padan.Model.Objects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void hoursOfReservationTest() {
        Reservation reservation = Reservation.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2).plusMinutes(30))
                .build();
        assertEquals(2.5, reservation.hoursReserved());
    }
}