package org.padan.Model.Repository;

import jakarta.persistence.*;
import org.padan.Model.Objects.Room;
import org.padan.Model.Objects.User;

import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<User> {
    @Override
    public void add(User obj, EntityManager em) {
        em.persist(obj);
    }

    @Override
    public void removeById(UUID obj, EntityManager em) {
        User user = em.find(User.class, obj, LockModeType.PESSIMISTIC_WRITE);
        if (user != null) {
            em.remove(user);
        }
    }

    @Override
    public User findById(UUID obj, EntityManager em) {
        return em.find(User.class, obj);
    }

    @Override
    public List<User> findAll(EntityManager em) {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public void updateElement(User newElement, UUID id, EntityManager em) {
        User user = em.find(User.class, id, LockModeType.PESSIMISTIC_WRITE);
        user.setEmail(newElement.getEmail());
        user.setFirstName(newElement.getFirstName());
        user.setLastName(newElement.getLastName());
        em.persist(user);
    }
}
