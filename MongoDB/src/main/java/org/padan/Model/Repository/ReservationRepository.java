package org.padan.Model.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.Reservation;
import org.padan.Model.Objects.Room;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository extends AbstractMongoRepository implements Repository<Reservation> {
    private final MongoCollection<Reservation> reservations;

    public ReservationRepository() {
        reservations = getRentAFieldDB().getCollection("reservations", Reservation.class);

    }

    @Override
    public void add(Reservation obj) {
        reservations.insertOne(obj);
    }

    @Override
    public void remove(ObjectId obj) {
        Bson filter = Filters.eq("_id", obj);
        reservations.deleteOne(filter);
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
    public void update(ObjectId id, Reservation obj) {

        Bson updateRoom = Updates.set("room", obj.getRoom());
        Bson updateUser = Updates.set("user", obj.getUser());
        Bson updateStartTime = Updates.set("start_time", obj.getStartTime());
        Bson updateEndTime = Updates.set("end_time", obj.getEndTime());

        reservations.updateOne(Filters.eq("_id", id), Updates.combine(updateUser, updateRoom,
                updateStartTime, updateEndTime));
    }

    @Override
    public void close() throws Exception {

    }
}
