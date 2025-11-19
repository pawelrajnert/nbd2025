package org.padan.Model.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.ObjectIdModule;
import org.padan.Model.Objects.Reservation;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RedisReservation implements RedisCache {
    private final Jedis jedis;
    private final ObjectMapper mapper;
    private static final String RESERVATIONS_KEY = "reservations:all";
    private static final String RESERVATION_KEY = "reservation:";

    public RedisReservation(Jedis jedis, ObjectMapper mapper) {
        this.jedis = jedis;
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = createDefaultMapper();
        }
    }

    public RedisReservation(Jedis jedis) {
        this(jedis, null);
    }


    private ObjectMapper createDefaultMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.registerModule(new ObjectIdModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return m;
    }

    private String getKey(ObjectId id) {
        return RESERVATION_KEY + id.toHexString();
    }

    @Override
    public Optional<Reservation> findById(ObjectId id) {
        try {
            String json = jedis.get(getKey(id));
            if (json != null) {
                return Optional.of(mapper.readValue(json, Reservation.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Reservation>> getAll() {
        try {
            String json = jedis.get(RESERVATIONS_KEY);
            if (json != null) {
                Reservation[] res = mapper.readValue(json, Reservation[].class);
                return Optional.of(Arrays.asList(res));
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void putOneInCache(ObjectId id, Reservation reservation, int ttl) {
        try {
            String json = mapper.writeValueAsString(reservation);
            jedis.setex(getKey(id), ttl, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putAllInCache(List<Reservation> reservations, int ttl) {
        try {
            String json = mapper.writeValueAsString(reservations);
            jedis.setex(RESERVATIONS_KEY, ttl, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOneInCache(ObjectId id) {
        try {
            jedis.del(getKey(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try {
            jedis.del(RESERVATIONS_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}