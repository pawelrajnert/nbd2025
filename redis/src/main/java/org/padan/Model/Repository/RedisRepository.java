package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.Reservation;

import java.util.List;
import java.util.Optional;

public class RedisRepository implements Repository<Reservation> {
    private final ReservationRepository reservation;
    private final RedisCache redisCache;
    private static final int TTL_ID = 60;
    private static final int TTL_ALL = 180;

    public RedisRepository(ReservationRepository reservation, RedisCache redisCache) {
        this.reservation = reservation;
        this.redisCache = redisCache;
    }

    @Override
    public void add(ClientSession session, Reservation obj) {
        reservation.add(session, obj);

        try {
            if (obj.getReservationId() != null) {
                redisCache.deleteOneInCache(obj.getReservationId());
            }
            redisCache.deleteAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(ClientSession session, ObjectId obj) {
        reservation.remove(session, obj);

        try {
            redisCache.deleteOneInCache(obj);
            redisCache.deleteAll();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Reservation findById(ObjectId id) {
        try {
            Optional<Reservation> res = redisCache.findById(id);
            if (res.isPresent()) {
                return res.get();
            }
        }
        catch (Exception e) {
            System.out.println("Nie znaleziono danych w redisie. Błąd funkcji findById().");
        }

        Reservation currentMongo = reservation.findById(id);
        if (currentMongo != null) {
            try {
                redisCache.putOneInCache(id, currentMongo, TTL_ID);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return currentMongo;
    }

    @Override
    public List<Reservation> findAll() {
        try {
            Optional<List<Reservation>> listToCache = redisCache.getAll();
            if (listToCache.isPresent()) {
                return listToCache.get();
            }
        }
        catch (Exception e) {
            System.out.println("Nie znaleziono danych w redisie. Błąd funkcji findAll().");;
        }
        List<Reservation> reservationsMongo = reservation.findAll();
        if (reservationsMongo != null) {
            redisCache.putAllInCache(reservationsMongo, TTL_ALL);
        }
        return reservationsMongo;
    }

    @Override
    public void update(ClientSession session, ObjectId id, Reservation obj) {
        reservation.update(session, id, obj);

        try {
            redisCache.deleteOneInCache(id);
            redisCache.deleteAll();

            Reservation updated = reservation.findById(id);
            if (updated != null) {
                redisCache.putOneInCache(id, updated, TTL_ID);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
