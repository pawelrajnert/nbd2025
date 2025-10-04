package org.padan.Model.Repository;

import java.util.List;

public interface Repository<T> {
    void add(T obj);

    void remove(T obj);

    void find(T obj);

    List<T> findAll();

    int getSize();

}
