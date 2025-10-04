package org.padan.Model.Repository;

import jakarta.persistence.*;
import org.padan.Model.Objects.Reservation;

import java.util.List;
import java.util.UUID;

public class ReservationRepository implements Repository<Reservation> {
    @Override
    public void add(Reservation obj, EntityManager em) {
        // Jest druga w nocy i już nie mogę myśleć, ale trzeba się zastanowić gdzie będziemy sprawdzać czy
        // rezerwacje nie kolidują ze sobą czasowo dla jednej sali - czy w managerach czy tutaj w repozytorium.
        // Albo teoretycznie można w obu miejscach, tak żeby na pewno dzialało, ale wtedy więcej testów trzeba.
        em.persist(obj);
    }

    @Override
    public void removeById(UUID obj, EntityManager em) {
        Reservation reservation = em.find(Reservation.class, obj, LockModeType.PESSIMISTIC_WRITE);
        if (reservation != null) {
            em.remove(reservation);
        }
    }

    @Override
    public Reservation findById(UUID obj, EntityManager em) {
        return em.find(Reservation.class, obj);
    }

    @Override
    public List<Reservation> findAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }

    @Override
    public void updateElement(Reservation newElement, UUID id, EntityManager em) {
        Reservation reservation = em.find(Reservation.class, id, LockModeType.PESSIMISTIC_WRITE);
        reservation.setRoom(newElement.getRoom());
        //TODO: skończyć tę metodę i sprawdzać kolizje
    }
}
