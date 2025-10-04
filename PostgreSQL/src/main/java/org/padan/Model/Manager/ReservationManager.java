package org.padan.Model.Manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.padan.Model.Objects.Reservation;
import org.padan.Model.Objects.Room;
import org.padan.Model.Repository.ReservationRepository;
import org.padan.Model.Repository.RoomRepository;
import org.padan.Model.Repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class ReservationManager {

    private final ReservationRepository repository;
    private final EntityManager em;
    private final EntityTransaction transaction;

    public ReservationManager (EntityManager em, EntityTransaction transaction) {
        repository = new ReservationRepository();
        this.em = em;
        this.transaction = transaction;
    }

    public void makeReservation() {
        //TODO: zrobić sprawdzenie na kolizję czasu
    }

    public void cancelReservation(UUID id) {
        transaction.begin();
        repository.removeById(id, em);
        transaction.commit();
    }

    public Reservation findReservation(UUID id) {
        return repository.findById(id, em);
    }

    public List<Reservation> getAllReservations() {
        return repository.findAll(em);
    }

    public void updateReservation(Reservation reservation, UUID id){
        transaction.begin();
        //TODO: sprawdzać kolizie (czy kolizje, nie wiem)
        repository.updateElement(reservation, id, em);
        transaction.commit();
    }
}
