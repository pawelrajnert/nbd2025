package org.padan.Model.Repository;

import org.bson.types.ObjectId;

import java.util.List;

public interface Repository<T> {
    void add(T obj);

    void remove(ObjectId obj);

    T findById(ObjectId id);

    List<T> findAll();

    void update(ObjectId id, T obj);

}
