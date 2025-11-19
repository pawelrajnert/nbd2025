package org.padan.Model.Objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @BsonId
    private ObjectId reservationId;
    @BsonProperty("room")
    private Room room;
    @BsonProperty("user")
    private User user;
    @BsonProperty("start_time")
    private LocalDateTime startTime;
    @BsonProperty("end_time")
    private LocalDateTime endTime;

    private double getPrice() {
        return room.getBasePrice();
    }

    private int hoursReserved() {
        return endTime.getHour() - startTime.getHour();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", room=" + room +
                ", user=" + user +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
