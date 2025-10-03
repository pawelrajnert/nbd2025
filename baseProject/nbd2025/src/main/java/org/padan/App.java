package org.padan;

import org.padan.Model.TrainerUser;
import org.padan.Model.User;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        User user = new TrainerUser("a", "b", "c", true);
        System.out.println(user.toString());
    }
}
