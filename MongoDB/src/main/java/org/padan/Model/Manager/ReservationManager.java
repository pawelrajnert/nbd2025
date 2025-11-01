package org.padan.Model.Manager;

import org.bson.types.ObjectId;
import org.padan.Model.Objects.RegularUser;
import org.padan.Model.Objects.Reservation;
import org.padan.Model.Repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationManager {
    private final ReservationRepository repository;

    public ReservationManager() {
        repository = new ReservationRepository();
    }

    public void makeReservation(Reservation reservation) {
        try {
            LocalDateTime start = reservation.getStartTime();
            LocalDateTime end = reservation.getEndTime();
            List<Reservation> allReservations = getAllReservations();

            if (start.isAfter(end) || start.equals(end)) {
                throw new Exception("Czas zakończenia rezerwacji jest niepoprawny.");
            }

            for (Reservation r : allReservations) {
                if (reservation.getRoom().equals(r.getRoom())) {
                    boolean timeCollision = start.isBefore(r.getEndTime()) && end.isAfter(r.getStartTime());

                    if (timeCollision) {
                        throw new Exception("W tym czasie istnieje już inna rezerwacja.");
                    }
                }
            }

            repository.add(reservation);
//            if (reservation.getUser() instanceof RegularUser){
//                ((RegularUser) reservation.getUser()).incrementLoyalty();
//            }
            System.out.println("Dodano nową rezerwację.");
        } catch (Exception e) {
            System.out.println("Nie można dokonać rezerwacji: " + e.getMessage());
        }
    }

    public void cancelReservation(ObjectId id) {
        repository.remove(id);
    }

    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    public void findReservation(ObjectId id) {
        repository.findById(id);
    }

}
