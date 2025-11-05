package org.padan.Model.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Room {
    @BsonId
    private ObjectId roomId;
    @BsonProperty("room_type")
    private RoomType roomType;
    @BsonProperty("capacity")
    private Integer capacity;
    @BsonProperty("base_price")
    private Double basePrice;
}
