package org.padan.Model.Objects;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reservationId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    // rozwiązanie z prezentacji
    //fetch z dokumentacji: (odrazu pobieramy dane)
    // Whether the association should be lazily loaded or must be eagerly fetched.
    // The EAGER strategy is a requirement on the persistence provider runtime that the associated entity must be eagerly fetched.
    // cascade z dokumentacji: (czy zarowno encja nadrzedna jak i podrzedna ma sie aktualizowac)
    // The operations that must be cascaded to the target of the association.
    private RoomDTO room;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private UserDTO user;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Column(name = "price")
    private Double price;

    private void calculateActualPrice() {
        price = user.getDiscount() * room.getBasePrice();
    }

    private int hoursReserved() {
        return endTime.getHour() - startTime.getHour();
    }

}
