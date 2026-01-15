package org.padan.Model.Manager;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.padan.Model.KafkaSetup.KafkaRentalProducer;
import org.padan.Model.Objects.RegularUser;
import org.padan.Model.Objects.Reservation;
import org.padan.Model.Repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationManager {
    private final ReservationRepository repository;
    private final MongoClient mongoClient;
    private final KafkaRentalProducer producer;

    public ReservationManager(ReservationRepository repository, MongoClient mongoClient) {
        this.repository = repository;
        this.mongoClient = mongoClient;
        this.producer = new KafkaRentalProducer();
    }

    public void makeReservation(Reservation reservation) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                LocalDateTime start = reservation.getStartTime();
                LocalDateTime end = reservation.getEndTime();
                List<Reservation> allReservations = getAllReservations();

                if (start.isAfter(end) || start.equals(end)) {
                    throw new Exception("Czas zakończenia rezerwacji jest niepoprawny.");
                }

                for (Reservation r : allReservations) {
                    if (reservation.getRoom().equals(r.getRoom())) {
                        boolean timeCollision = start.isBefore(r.getEndTime()) && end.isAfter(r.getStartTime());
                        if (timeCollision) {
                            throw new Exception("W tym czasie istnieje już inna rezerwacja.");
                        }
                    }
                }

                repository.add(session, reservation);

                if (reservation.getUser() instanceof RegularUser) {
                    ((RegularUser) reservation.getUser()).incrementLoyalty();
                }

                session.commitTransaction();
                producer.sendReservation(reservation);

            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot add reservation", e);
            }
        }

    }

    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    public Reservation findReservation(ObjectId id) {
        return repository.findById(id);
    }

    public void removeReservation(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong reservation id");
        }

        if (findReservation(id) == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.remove(session, id);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot remove reservation", e);
            }
        }
    }

    public void updateReservation(ObjectId id, Reservation res) {
        if (id == null) {
            throw new IllegalArgumentException("Wrong reservation id");
        }
        if (findReservation(id) == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();
            try {
                repository.update(session, id, res);
                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction();
                throw new RuntimeException("Cannot update reservation", e);
            }
        }
    }


}
