package org.padan.Model.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.padan.Model.Objects.*;

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractMongoRepository implements Repository<User> {

    MongoCollection<User> users;
    public UserRepository() {
        users = getRentAFieldDB().getCollection("users", User.class);
    }

    @Override
    public void add(User obj) {
        users.insertOne(obj);
    }

    @Override
    public void remove(ObjectId obj) {
        Bson filter = Filters.eq("_id", obj);
        users.deleteOne(filter);
    }

    @Override
    public User findById(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        return users.find(filter).first();

    }

    @Override
    public List<User> findAll() {
        return users.find().into(new ArrayList<>());
    }

    @Override
    public void update(ObjectId id, User obj) {
        Bson updateName = Updates.set("first_name", obj.getFirstName());
        Bson updateEmail = Updates.set("email", obj.getEmail());
        Bson updateLastName = Updates.set("last_name", obj.getLastName());
        if (obj instanceof RegularUser) {
            Bson updateLoyalty = Updates.set("loyalty_counter", ((RegularUser) obj).getLoyaltyCounter());
            users.updateOne(Filters.eq("_id", id), updateLoyalty);
        }
        if (obj instanceof TrainerUser) {
            Bson updateIsPartner = Updates.set("is_partner", ((TrainerUser) obj).getIsPartner());
            users.updateOne(Filters.eq("_id", id), updateIsPartner);
        }
        users.updateOne(Filters.eq("_id", id), Updates.combine(updateEmail, updateName, updateLastName));
    }

    @Override
    public void close() throws Exception {
        getMongoClient().close();
    }
}
