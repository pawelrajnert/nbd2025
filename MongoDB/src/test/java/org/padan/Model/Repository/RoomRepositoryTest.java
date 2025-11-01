package org.padan.Model.Repository;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Objects.Room;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.padan.Model.Objects.RoomType.COURT;

class RoomRepositoryTest {

    RoomRepository repository;

    Room testRoom;
    ObjectId testId;

    @BeforeEach
    void setUp() {
        testId = new ObjectId("000000000000000000000000");
        repository = new RoomRepository();

        testRoom = Room.builder()
                .roomType(COURT)
                .basePrice(12d)
                .capacity(20)
                .roomId(testId)
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.remove(testId);
    }

    @Test
    void testAddFind() {
        int countBefore = repository.findAll().size();

        assertDoesNotThrow(()->repository.add(testRoom));
        assertTrue(countBefore < repository.findAll().size());
        assertFalse(repository.findAll().isEmpty());
        assertEquals(COURT, repository.findById(testId).getRoomType());
        assertEquals(20, repository.findById(testId).getCapacity());
        assertEquals(12d, repository.findById(testId).getBasePrice());
    }


    @Test
    void testUpdate(){
        int testCap = 50;
        repository.add(testRoom);
        Room room = repository.findById(testId);
        room.setCapacity(testCap);
        repository.update(testId, room);
        assertEquals(testCap, repository.findById(testId).getCapacity());
    }

    @Test
    void testRemove() {
        repository.add(testRoom);
        int countBefore = repository.findAll().size();
        repository.remove(testId);
        assertTrue(countBefore > repository.findAll().size());
    }
}