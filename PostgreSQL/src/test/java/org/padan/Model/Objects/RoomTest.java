package org.padan.Model.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.padan.BaseTC;

public class RoomTest extends BaseTC {
    private Room room1;
    private Room room2;
    private Room room3;
    private Room room4;

    @BeforeEach
    public void createExampleRooms() {
        room1 = new Room(RoomType.GYM, 50, 30.);
        room2 = new Room(RoomType.FIELD, 40, 300.);
        room3 = new Room(RoomType.HALL, 22, 150.);
        room4 = new Room(RoomType.COURT, 2, 200.);

        rm.addRoom(room1);
        rm.addRoom(room2);
        rm.addRoom(room3);
        rm.addRoom(room4);
    }

    @Test
    public void creationRoomTest() {
        assertNotNull(room1.getRoomId());
        assertNotNull(room2.getRoomId());
        assertNotNull(room3.getRoomId());
        assertNotNull(room4.getRoomId());

        assertEquals(RoomType.GYM, room1.getRoomType());
        assertEquals(50, room1.getCapacity());
        assertEquals(30, room1.getBasePrice());

        assertEquals(RoomType.FIELD, room2.getRoomType());
        assertEquals(40, room2.getCapacity());
        assertEquals(300, room2.getBasePrice());

        assertEquals(RoomType.HALL, room3.getRoomType());
        assertEquals(22, room3.getCapacity());
        assertEquals(150, room3.getBasePrice());

        assertEquals(RoomType.COURT, room4.getRoomType());
        assertEquals(2, room4.getCapacity());
        assertEquals(200, room4.getBasePrice());
    }

    @Test
    public void updateRoomsTest() {
        room1.setRoomType(RoomType.COURT);
        room1.setBasePrice(100.);
        room1.setCapacity(10);

        room2.setRoomType(RoomType.GYM);
        room2.setBasePrice(100.);
        room2.setCapacity(10);

        room3.setRoomType(RoomType.GYM);
        room3.setBasePrice(100.);
        room3.setCapacity(10);

        room4.setRoomType(RoomType.GYM);
        room4.setBasePrice(100.);
        room4.setCapacity(10);

        assertEquals(RoomType.COURT, room1.getRoomType());
        assertEquals(100, room1.getBasePrice());
        assertEquals(10, room1.getCapacity());

        assertEquals(RoomType.GYM, room2.getRoomType());
        assertEquals(100, room2.getBasePrice());
        assertEquals(10, room2.getCapacity());

        assertEquals(RoomType.GYM, room3.getRoomType());
        assertEquals(100, room3.getBasePrice());
        assertEquals(10, room3.getCapacity());

        assertEquals(RoomType.GYM, room4.getRoomType());
        assertEquals(100, room4.getBasePrice());
        assertEquals(10, room4.getCapacity());
    }

    @Test
    public void deleteRooms() {
        rm.removeRoom(room1.getRoomId());
        rm.removeRoom(room2.getRoomId());
        rm.removeRoom(room3.getRoomId());
        rm.removeRoom(room4.getRoomId());

        assertNull(rm.findRoom(room1.getRoomId()));
        assertNull(rm.findRoom(room2.getRoomId()));
        assertNull(rm.findRoom(room3.getRoomId()));
        assertNull(rm.findRoom(room4.getRoomId()));
    }
}
