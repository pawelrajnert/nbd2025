package org.padan.Model;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Reservation {
    private UUID reservationId;
    private RoomDTO room;
    private UserDTO user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;

    private void calculateActualPrice(){
        price = user.getDiscount() * room.getBasePrice();
    }

    private int hoursReserved(){
        return endTime.getHour() - startTime.getHour();
    }

}
