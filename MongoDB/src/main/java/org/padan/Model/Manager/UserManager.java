package org.padan.Model.Manager;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.User;
import org.padan.Model.Repository.UserRepository;

import java.util.List;

public class UserManager {
    private final UserRepository repository;
    private final MongoClient mongoClient;

    public UserManager(UserRepository repository, MongoClient mongoClient) {
        this.repository = repository;
        this.mongoClient = mongoClient;
    }

    public void registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.add(session, user);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot add user", e);
            }
        }
    }

    public User findUser(ObjectId id) {
        return repository.findById(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void removeUser(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong user id");
        }

        if (findUser(id) == null) {
            throw new IllegalArgumentException("User not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.remove(session, id);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot remove user", e);
            }
        }
    }

    public void updateUser(ObjectId id, User user) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong user id");
        }
        if (findUser(id) == null) {
            throw new IllegalArgumentException("User not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.update(session, id, user);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot update user", e);
            }
        }
    }
}
