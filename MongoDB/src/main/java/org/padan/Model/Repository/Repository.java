package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import org.bson.types.ObjectId;

import java.util.List;

public interface Repository<T> {
    void add(ClientSession session, T obj);

    void remove(ClientSession session, ObjectId obj);

    T findById(ObjectId id);

    List<T> findAll();

    void update(ClientSession session, ObjectId id, T obj);

}
