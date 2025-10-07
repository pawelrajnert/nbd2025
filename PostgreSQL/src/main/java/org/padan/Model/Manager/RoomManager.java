package org.padan.Model.Manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.padan.Model.Objects.Room;
import org.padan.Model.Repository.RoomRepository;

import java.util.List;
import java.util.UUID;

public class RoomManager {
    private final RoomRepository repository;
    private final EntityManager em;
    private final EntityTransaction transaction;

    public RoomManager(EntityManager em, EntityTransaction transaction) {
        repository = new RoomRepository();
        this.em = em;
        this.transaction = transaction;
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

    public Room findRoom(UUID id) {
        return repository.findById(id, em);
    }

    public List<Room> getAllRooms() {
        return repository.findAll(em);
    }

    public void updateRoom(Room room, UUID id) {
        transaction.begin();
        repository.updateElement(room, id, em);
        transaction.commit();
    }
}
