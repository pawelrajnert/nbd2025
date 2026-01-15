package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.Reservation;

import java.util.ArrayList;
import java.util.List;

// POPRAWKA 1: Dodano extends i implements
public class ReservationRepository extends AbstractMongoRepository implements Repository<Reservation> {

    private final MongoCollection<Reservation> reservations;

    // POPRAWKA 2: Konstruktor bez argumentów korzystający z metody rodzica
    public ReservationRepository() {
        // getRentAFieldDB() pochodzi z AbstractMongoRepository
        this.reservations = getRentAFieldDB().getCollection("reservations", Reservation.class);
    }

    @Override
    public void add(ClientSession session, Reservation obj) {
        reservations.insertOne(session, obj);
    }

    @Override
    public void remove(ClientSession session, ObjectId obj) {
        Bson filter = Filters.eq("_id", obj);
        reservations.deleteOne(session, filter);
    }

    @Override
    public Reservation findById(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        return reservations.find(filter).first();
    }

    @Override
    public List<Reservation> findAll() {
        return reservations.find().into(new ArrayList<>());
    }

    @Override
    public void update(ClientSession session, ObjectId id, Reservation obj) {
        Bson updateRoom = Updates.set("room", obj.getRoom());
        Bson updateUser = Updates.set("user", obj.getUser());
        Bson updateStartTime = Updates.set("start_time", obj.getStartTime());
        Bson updateEndTime = Updates.set("end_time", obj.getEndTime());

        reservations.updateOne(session, Filters.eq("_id", id), Updates.combine(updateUser, updateRoom,
                updateStartTime, updateEndTime));
    }
}