package org.padan.Model.Repository;

import jakarta.persistence.*;
import org.padan.Model.Objects.Room;

import java.util.List;
import java.util.UUID;

public class RoomRepository implements Repository<Room> {
    @Override
    public void add(Room obj, EntityManager em) {
        em.persist(obj);
    }

    @Override
    public void removeById(UUID obj, EntityManager em) {
        Room room = em.find(Room.class, obj, LockModeType.PESSIMISTIC_WRITE);
        if (room != null) {
            em.remove(room);
        }
    }

    @Override
    public Room findById(UUID obj, EntityManager em) {
        return em.find(Room.class, obj);
    }

    @Override
    public List<Room> findAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Room r", Room.class).getResultList();
    }

    @Override
    public void updateElement(Room newElement, UUID id, EntityManager em) {
        Room room = em.find(Room.class, id, LockModeType.PESSIMISTIC_WRITE);
        room.setRoomType(newElement.getRoomType());
        room.setBasePrice(newElement.getBasePrice());
        room.setCapacity(newElement.getCapacity());
        em.persist(room);
    }
}
