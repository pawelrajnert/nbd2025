package org.padan;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.padan.Model.Objects.TrainerUser;
import org.padan.Model.Objects.User;
import org.padan.Model.Repository.UserRepository;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        User user = new TrainerUser("a", "b", "c", true);
        System.out.println(user.toString());
    }
}
