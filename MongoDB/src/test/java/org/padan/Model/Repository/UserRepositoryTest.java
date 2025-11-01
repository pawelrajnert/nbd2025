package org.padan.Model.Repository;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Objects.RegularUser;
import org.padan.Model.Objects.StudentUser;
import org.padan.Model.Objects.User;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    UserRepository repository;

    User testUser;
    ObjectId testId;

    @BeforeEach
    void setUp() {
        testId = new ObjectId("000000000000000000000000");
        repository = new UserRepository();

        repository.remove(testId);

        testUser = new RegularUser("name", "surname", "email");
        testUser.setUserId(testId);
    }

    @AfterEach
    void tearDown() {
        repository.remove(testId);
    }

    @Test
    void testAddFind() {
        assertDoesNotThrow(()->repository.add(testUser));
        assertFalse(repository.findAll().isEmpty());
        assertInstanceOf(RegularUser.class, repository.findById(testId));
        assertEquals("name", repository.findById(testId).getFirstName());
        assertEquals("surname", repository.findById(testId).getLastName());
        assertEquals("email", repository.findById(testId).getEmail());
    }


    @Test
    void testUpdate(){
        String test = "test_change";
        repository.add(testUser);
        User user = repository.findById(testId);
        user.setFirstName(test);
        repository.update(testId, user);
        assertEquals(test, repository.findById(testId).getFirstName());
    }

    @Test
    void testRemove() {
        repository.add(testUser);
        int countBefore = repository.findAll().size();
        repository.remove(testId);
        assertTrue(countBefore > repository.findAll().size());
    }
}