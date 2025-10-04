package org.padan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.padan.Model.Manager.UserManager;
import org.padan.Model.Objects.TrainerUser;
import org.padan.Model.Objects.User;
import org.padan.Model.Repository.UserRepository;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbddb")) {
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();

            UserManager manager = new UserManager(em, transaction);
            User user = new TrainerUser("a", "b", "c", true);
            manager.registerUser(user);
        }

    }
}
