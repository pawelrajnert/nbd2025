package org.padan.Model.Repository;

import org.bson.types.ObjectId;
import org.padan.Model.Objects.Reservation;

import java.util.List;
import java.util.Optional;

public interface RedisCache {
    Optional<Reservation> findById(ObjectId id);

    Optional<List<Reservation>> getAll();

    void putOneInCache(ObjectId id, Reservation reservation, int ttl);

    void putAllInCache(List<Reservation> reservations, int ttl);

    void deleteOneInCache(ObjectId id);

    void deleteAll();
}
