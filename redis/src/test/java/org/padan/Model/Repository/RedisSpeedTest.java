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
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 15, time = 1)
@Fork(1)
public class RedisSpeedTest {
    private ObjectId idInCache;
    private ObjectId idNotInCache;
    private ObjectId idMongo;

    private RedisRepository redisRepository;
    private RedisReservation redisCache;
    private ReservationRepository mongoRepository;

    private Reservation testReservation1;
    private Reservation testReservation2;
    private Reservation testReservation3;

    @Setup(Level.Trial)
    public void init() {
        redisCache = new RedisReservation(RedisConnect.startConnection());
        mongoRepository = new ReservationRepository();
        redisRepository = new RedisRepository(mongoRepository, redisCache);

        idInCache = new ObjectId("111111111111111111111111");
        idNotInCache = new ObjectId("222222222222222222222222");
        idMongo = new ObjectId("333333333333333333333333");

        testReservation1 = createTestReservation(idInCache);
        testReservation2 = createTestReservation(idNotInCache);
        testReservation3 = createTestReservation(idMongo);

        try {
            RedisConnect.startConnection().flushAll();
            ClientSession session = mongoRepository.startSession();
            try {
                mongoRepository.add(session, testReservation1);
                mongoRepository.add(session, testReservation2);
                mongoRepository.add(session, testReservation3);
            } finally {
                session.close();
            }

            redisCache.putOneInCache(idInCache, testReservation1, 3600);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @TearDown(Level.Trial)
    public void cleanup() {
        try {
            RedisConnect.startConnection().flushAll();

            ClientSession session = mongoRepository.startSession();
            try {
                mongoRepository.remove(session, idInCache);
                mongoRepository.remove(session, idNotInCache);
                mongoRepository.remove(session, idMongo);
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
    public void directMongoRead(Blackhole blackhole) {
        blackhole.consume(mongoRepository.findById(idMongo));
    }

    @Benchmark
    public void redisInCache(Blackhole blackhole) {
        blackhole.consume(redisCache.findById(idInCache));
    }

    @Benchmark
    public void redisNotInCache(Blackhole blackhole) {
        try {
            redisCache.deleteOneInCache(idNotInCache);
        } catch (Exception e) {

        }
        blackhole.consume(redisRepository.findById(idNotInCache));
    }

    private Reservation createTestReservation(ObjectId id) {
        Room testRoom = Room.builder()
                .roomId(id)
                .basePrice(100.0)
                .roomType(COURT)
                .capacity(20)
                .build();

        User testUser = new StudentUser("Test", "User", "test@example.com");
        testUser.setUserId(id);

        return Reservation.builder()
                .reservationId(id)
                .room(testRoom)
                .user(testUser)
                .startTime(LocalDateTime.of(2025, 10, 11, 15, 0))
                .endTime(LocalDateTime.of(2025, 10, 11, 17, 0))
                .build();
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}