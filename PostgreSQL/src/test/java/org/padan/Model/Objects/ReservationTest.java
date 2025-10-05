package org.padan.Model.Objects;

import org.junit.jupiter.api.Test;
import org.padan.BaseTC;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest extends BaseTC {

    @Test
    void hoursOfReservationTest() {
        Reservation reservation = Reservation.builder()
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2).plusMinutes(30))
                .build();
        assertEquals(2.5, reservation.hoursReserved());
    }
}