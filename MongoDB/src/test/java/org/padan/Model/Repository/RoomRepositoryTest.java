package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.padan.Model.Manager.RoomManager;
import org.padan.Model.Objects.Room;
import org.padan.Model.Objects.RoomType;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.padan.Model.Objects.RoomType.*;

class RoomRepositoryTest {
    private RoomRepository repository;
    private ClientSession session;
    private ObjectId testId;
    private Room testRoom;
    private RoomManager roomManager;

    @BeforeEach
    void setUp() {
        repository = new RoomRepository();
        session = repository.startSession();
        roomManager = new RoomManager(repository, repository.getMongoClient());

        try {
            for (Room r : repository.findAll()) {
                roomManager.removeRoom(r.getRoomId());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        testId = new ObjectId("100000000000000000000000");


        testRoom = Room.builder()
                .roomId(testId)
                .basePrice(12d)
                .roomType(COURT)
                .capacity(20)
                .build();
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            try {
                roomManager.removeRoom(testId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                session.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (repository != null) {
            try {
                for (Room r : repository.findAll()) {
                    roomManager.removeRoom(r.getRoomId());
                }
                repository.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    void addRoom() {
        assertEquals(0, repository.findAll().size());

        roomManager.registerRoom(testRoom);

        assertEquals(1, repository.findAll().size());

        Room tr;

        ObjectId newId = new ObjectId("110000000000000000000000");

        tr = Room.builder()
                .roomId(newId)
                .basePrice(150d)
                .roomType(HALL)
                .capacity(40)
                .build();


        roomManager.registerRoom(tr);

        assertEquals(2, repository.findAll().size());
        assertEquals(tr.getRoomId(), repository.findById(tr.getRoomId()).getRoomId());
        assertEquals(tr.getRoomType(), repository.findById(tr.getRoomId()).getRoomType());
        assertEquals(tr.getCapacity(), repository.findById(tr.getRoomId()).getCapacity());
        assertEquals(tr.getBasePrice(), repository.findById(tr.getRoomId()).getBasePrice());
    }

    @Test
    void updateRoom() {
        roomManager.registerRoom(testRoom);

        testRoom.setRoomType(FIELD);
        roomManager.updateRoom(testRoom.getRoomId(), testRoom);
        assertEquals(testRoom.getRoomType(), repository.findById(testRoom.getRoomId()).getRoomType());
    }

    @Test
    void deleteRoom() {
        assertEquals(0, repository.findAll().size());

        roomManager.registerRoom(testRoom);
        assertEquals(1, repository.findAll().size());

        roomManager.removeRoom(testId);
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void fromPojoTest() {
        roomManager.registerRoom(testRoom);

        MongoCollection<Document> raw = repository.getRentAFieldDB().getCollection("rooms", Document.class);
        Document doc = raw.find(eq("_id", testId)).first();

        assertNotNull(doc);
        assertEquals("COURT", doc.getString("room_type"));
        assertEquals(20, doc.getInteger("capacity"));
        assertEquals(12.0, doc.getDouble("base_price"));
    }

    @Test
    void toPojoTest() {
        roomManager.registerRoom(testRoom);

        Room fromDb = repository.findById(testId);
        assertNotNull(fromDb);
        assertEquals(RoomType.COURT, fromDb.getRoomType());
        assertEquals(20, fromDb.getCapacity());
        assertEquals(12.0, fromDb.getBasePrice());
    }
}
