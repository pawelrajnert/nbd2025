package org.padan.Model.Manager;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.Room;
import org.padan.Model.Repository.RoomRepository;

import java.util.List;

public class RoomManager {
    private final RoomRepository repository;
    private final MongoClient mongoClient;

    public RoomManager(RoomRepository repository, MongoClient mongoClient) {
        this.repository = repository;
        this.mongoClient = mongoClient;
    }

    public void registerRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException();
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.add(session, room);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot add room", e);
            }
        }
    }

    public Room findRoom(ObjectId id) {
        return repository.findById(id);
    }

    public List<Room> findAll() {
        return repository.findAll();
    }

    public void removeRoom(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong room id");
        }

        if (findRoom(id) == null) {
            throw new IllegalArgumentException("Room not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.remove(session, id);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot remove room", e);
            }
        }
    }

    public void updateRoom(ObjectId id, Room room) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong room id");
        }
        if (findRoom(id) == null) {
            throw new IllegalArgumentException("Room not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.update(session, id, room);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot update room", e);
            }
        }
    }
}
