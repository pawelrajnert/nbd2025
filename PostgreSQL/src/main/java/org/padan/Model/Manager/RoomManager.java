package org.padan.Model.Manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.padan.Model.Objects.Room;
import org.padan.Model.Repository.RoomRepository;

import java.util.List;
import java.util.UUID;

public class RoomManager {
    private final RoomRepository repository;
    private final EntityManager em;
    private final EntityTransaction transaction;

    public RoomManager() {
        repository = new RoomRepository();
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbddb")) {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
        }
    }

    public void addRoom(Room obj) {
        transaction.begin();
        repository.add(obj, em);
        transaction.commit();

    }

    public void removeRoom(UUID id) {
        transaction.begin();
        repository.removeById(id, em);
        transaction.commit();
    }

    public void findRoom(UUID id) {
        repository.findById(id, em);
    }

    public List<Room> getAllRooms() {
        return repository.findAll(em);
    }

    public void updateRoom(Room room, UUID id){
        transaction.begin();
        repository.updateElement(room, id, em);
        transaction.commit();
    }
}
