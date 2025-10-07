package org.padan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.padan.Model.Manager.ReservationManager;
import org.padan.Model.Manager.RoomManager;
import org.padan.Model.Manager.UserManager;
import org.padan.Model.Objects.*;

import java.time.LocalDateTime;
import java.util.List;

public class App {
    public static void main(String[] args) {
        /* try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbddb");
            EntityManager em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();

            UserManager um = new UserManager(em, transaction);
            RoomManager rm = new RoomManager(em, transaction);
            ReservationManager resm = new ReservationManager(em, transaction);

            // tworzymy 3 przykladowych uzytkownikow
            User user1 = new TrainerUser("abdddc", "abc", "abc@gmail.com", true);
            User user2 = new RegularUser("abd", "abd", "abd@gmail.com", 5);
            User user3 = new StudentUser("abe", "abe", "abe@gmail.com");

            um.registerUser(user1);
            um.registerUser(user2);
            um.registerUser(user3);

            // tworzymy 2 przykladowe boiska/miejsca cwiczen
            Room room1 = new Room(RoomType.GYM, 10, 50.0);
            Room room2 = new Room(RoomType.COURT, 2, 150.0);

            rm.addRoom(room1);
            rm.addRoom(room2);

            // tworzymy 3 przykladowe rezerwacje
            LocalDateTime start1 = LocalDateTime.of(2025, 10, 10, 15, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 10, 10, 17, 0);
            Reservation res1 = new Reservation(room1, user1, start1, end1, room1.getBasePrice());
            resm.makeReservation(res1);

            // tu oczekujemy ze nie przejdzie, bo rezerwujemy to samo boisko co w przypadku 1
            LocalDateTime start2 = LocalDateTime.of(2025, 10, 10, 15, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 10, 10, 17, 0);
            Reservation res2 = new Reservation(room1, user2, start2, end2, room1.getBasePrice());
            resm.makeReservation(res2);

            // tutaj za to zmieniamy boisko i powinno przejść
            res2.setRoom(room2);
            res2.setPrice(room2.getBasePrice());
            resm.makeReservation(res2);

            LocalDateTime start3 = LocalDateTime.of(2025, 10, 19, 15, 0);
            LocalDateTime end3 = LocalDateTime.of(2025, 10, 19, 17, 0);
            Reservation res3 = new Reservation(room1, user3, start3, end3, room1.getBasePrice());
            resm.makeReservation(res3);

            // sprawdzamy edycje rezerwacji
            LocalDateTime start4 = LocalDateTime.of(2025, 10, 25, 15, 0);
            LocalDateTime end4 = LocalDateTime.of(2025, 10, 25, 17, 0);
            resm.updateReservation(res3, res3.getReservationId(),start4,end4);


        } catch (Exception e) {
            e.printStackTrace();
        } */
    }
}
