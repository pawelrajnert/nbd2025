package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository extends AbstractMongoRepository implements Repository<Room> {

    MongoCollection<Room> rooms;

    public RoomRepository() {
        rooms = getRentAFieldDB().getCollection("rooms", Room.class);
    }

    @Override
    public void add(ClientSession session, Room obj) {
        rooms.insertOne(session, obj);
    }

    @Override
    public void remove(ClientSession session, ObjectId obj) {
        Bson filter = Filters.eq("_id", obj);
        rooms.deleteOne(session, filter);
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
    public void update(ClientSession session, ObjectId id, Room obj) {
        Bson updateCapacity = Updates.set("capacity", obj.getCapacity());
        Bson updatePrice = Updates.set("base_price", obj.getBasePrice());
        Bson updateRoomType = Updates.set("room_type", obj.getRoomType());
        rooms.updateOne(session, Filters.eq("_id", id), Updates.combine(updatePrice, updateCapacity, updateRoomType));
    }
}
