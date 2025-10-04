package org.padan.Model.Manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.padan.Model.Objects.Room;
import org.padan.Model.Objects.User;
import org.padan.Model.Repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserManager {

    private final UserRepository repository;
    private final EntityManager em;
    private final EntityTransaction transaction;

    public UserManager(EntityManager em, EntityTransaction transaction) {
        repository = new UserRepository();
        this.em = em;
        this.transaction = transaction;
    }

    public void registerUser(User user) {
        transaction.begin();
        repository.add(user, em);
        transaction.commit();
    }

    public User findUsers(UUID id) {
        return repository.findById(id, em);
    }

    public List<User> getAllUsers() {
        return repository.findAll(em);
    }

    public void removeUser(UUID id) {
        transaction.begin();
        repository.removeById(id, em);
        transaction.commit();
    }
    public void updateUser(User user, UUID id){
        transaction.begin();
        repository.updateElement(user, id, em);
        transaction.commit();
    }
}
