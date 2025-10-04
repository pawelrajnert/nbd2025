package org.padan.Model.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class RoomDTO {
    private UUID roomId;
    private RoomType roomType;
    private Integer capacity;
    private Double basePrice;
}
