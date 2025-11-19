package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.padan.Model.Objects.*;
import org.padan.Model.RedisConnect;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.padan.Model.Objects.RoomType.COURT;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class RedisSpeedTest {
    private RedisReservation reservationRedis;
    private ReservationRepository reservationRepository;
    private ObjectId id;
    private Reservation testReservation;

    @Setup(Level.Trial)
    public void init() {
        System.out.println("=== Initializing benchmark ===");

        reservationRedis = new RedisReservation(RedisConnect.startConnection());
        reservationRepository = new ReservationRepository();

        id = new ObjectId("000000000000000000000000");

        Room testRoom = Room.builder()
                .roomId(id)
                .basePrice(12d)
                .roomType(COURT)
                .capacity(20)
                .build();

        User testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(id);

        LocalDateTime start = LocalDateTime.of(2025, 10, 11, 15, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 11, 17, 0);

        testReservation = Reservation.builder()
                .reservationId(id)
                .room(testRoom)
                .user(testUser)
                .startTime(start)
                .endTime(end)
                .build();

        try {
            RedisConnect.startConnection().flushAll();
            reservationRedis.putOneInCache(id, testReservation, 3600);
            System.out.println("Added to redis");

            ClientSession session = reservationRepository.startSession();
            try {
                try {
                    reservationRepository.remove(session, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                reservationRepository.add(session, testReservation);
                System.out.println("Added to mongo");

            } finally {
                session.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JMH setup failed", e);
        }
    }

    @TearDown(Level.Trial)
    public void cleanup() {
        try {
            RedisConnect.startConnection().flushAll();

            ClientSession session = reservationRepository.startSession();
            try {
                reservationRepository.remove(session, id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void readFromRedis(Blackhole blackhole) {
        blackhole.consume(reservationRedis.findById(id));
    }

    @Benchmark
    public void readFromMongo(Blackhole blackhole) {
        blackhole.consume(reservationRepository.findById(id));
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}