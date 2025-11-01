package org.padan.Model.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.RegularUser;
import org.padan.Model.Objects.Room;
import org.padan.Model.Objects.TrainerUser;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository extends AbstractMongoRepository implements Repository<Room> {

    MongoCollection<Room> rooms;

    public RoomRepository() {
        rooms = getRentAFieldDB().getCollection("rooms", Room.class);
    }

    @Override
    public void add(Room obj) {
        rooms.insertOne(obj);
    }

    @Override
    public void remove(ObjectId obj) {
        Bson filter = Filters.eq("_id", obj);
        rooms.deleteOne(filter);
    }

    @Override
    public Room findById(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        return rooms.find(filter).first();
    }

    @Override
    public List<Room> findAll() {
        return rooms.find().into(new ArrayList<>());
    }

    @Override
    public void update(ObjectId id, Room obj) {
        Bson updateCapacity = Updates.set("capacity", obj.getCapacity());
        Bson updatePrice = Updates.set("base_price", obj.getBasePrice());
        Bson updateRoomType = Updates.set("room_type", obj.getRoomType());
        rooms.updateOne(Filters.eq("_id", id), Updates.combine(updatePrice, updateCapacity, updateRoomType));
    }

    @Override
    public void close() throws Exception {

    }
}
