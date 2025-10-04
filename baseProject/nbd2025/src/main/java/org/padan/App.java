package org.padan;

import org.padan.Model.TrainerUserDTO;
import org.padan.Model.UserDTO;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        UserDTO user = new TrainerUserDTO("a", "b", "c", true);
        System.out.println(user.toString());
    }
}
