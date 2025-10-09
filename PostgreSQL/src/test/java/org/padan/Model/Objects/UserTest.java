package org.padan.Model.Objects;

import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.BaseTC;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest extends BaseTC {

    private User student1;
    private User regular1;
    private User trainer1;

    @BeforeEach
    public void createExampleUsers() {
        student1 = new StudentUser("Student1", "Student1", "student1@gmail.com");
        regular1 = new RegularUser("Regular1", "Regular1", "regular1@gmail.com", 2);
        trainer1 = new TrainerUser("Trainer1", "Trainer1", "trainer1@gmail.com", true);

        um.registerUser(student1);
        um.registerUser(regular1);
        um.registerUser(trainer1);
    }

    @Test
    public void createStudentTest() {
        assertNotNull(student1.getUserId());
        assertEquals("Student1", student1.getFirstName());
        assertEquals("Student1", student1.getLastName());
        assertEquals("student1@gmail.com", student1.getEmail());
        assertEquals("StudentUser", student1.getTypeName());
        // student ma rabat w wysokosci 25%
        assertEquals(0.25, student1.getDiscount());
    }

    @Test
    public void createRegularUserTest() {
        assertNotNull(regular1.getUserId());
        assertEquals("Regular1", regular1.getFirstName());
        assertEquals("Regular1", regular1.getLastName());
        assertEquals("regular1@gmail.com", regular1.getEmail());
        assertEquals("RegularUser", regular1.getTypeName());
        // loyaltycounter = 2 oznacza 2% rabat
        assertEquals((2 * 0.01), regular1.getDiscount());
    }

    @Test
    public void createTrainerTest() {
        assertNotNull(trainer1.getUserId());
        assertEquals("Trainer1", trainer1.getFirstName());
        assertEquals("Trainer1", trainer1.getLastName());
        assertEquals("trainer1@gmail.com", trainer1.getEmail());
        assertEquals("TrainerUser", trainer1.getTypeName());
        // jesli trener jest partnerem, to ma znizke 50%
        assertEquals(0.5, trainer1.getDiscount());
    }

    @Test
    public void editStudentAll() {
        assertEquals("Student1", student1.getFirstName());
        assertEquals("Student1", student1.getLastName());
        assertEquals("student1@gmail.com", student1.getEmail());

        student1.setFirstName("abc");
        student1.setLastName("def");
        student1.setEmail("abcdef@gmail.com");

        assertEquals("abc", student1.getFirstName());
        assertEquals("def", student1.getLastName());
        assertEquals("abcdef@gmail.com", student1.getEmail());
        assertEquals("StudentUser", student1.getTypeName());
        assertEquals(0.25, student1.getDiscount());
    }

    @Test
    public void editRegularUserAll() {
        assertEquals("Regular1", regular1.getFirstName());
        assertEquals("Regular1", regular1.getLastName());
        assertEquals("regular1@gmail.com", regular1.getEmail());
        assertEquals((2 * 0.01), regular1.getDiscount());

        regular1.setFirstName("abc");
        regular1.setLastName("def");
        regular1.setEmail("abcdef@gmail.com");
        ((RegularUser) regular1).incrementLoyalty();

        assertEquals("abc", regular1.getFirstName());
        assertEquals("def", regular1.getLastName());
        assertEquals("abcdef@gmail.com", regular1.getEmail());
        assertEquals("RegularUser", regular1.getTypeName());
        assertEquals((3 * 0.01), regular1.getDiscount());
    }

    @Test
    public void editTrainerAll() {
        assertEquals("Trainer1", trainer1.getFirstName());
        assertEquals("Trainer1", trainer1.getLastName());
        assertEquals("trainer1@gmail.com", trainer1.getEmail());
        assertEquals((0.5), trainer1.getDiscount());
        assertEquals(true, ((TrainerUser) trainer1).getIsPartner());

        trainer1.setFirstName("abc");
        trainer1.setLastName("def");
        trainer1.setEmail("abcdef@gmail.com");
        ((TrainerUser) trainer1).setIsPartner(false);

        assertEquals("abc", trainer1.getFirstName());
        assertEquals("def", trainer1.getLastName());
        assertEquals("abcdef@gmail.com", trainer1.getEmail());
        assertEquals("TrainerUser", trainer1.getTypeName());
        assertEquals(false, ((TrainerUser) trainer1).getIsPartner());
        assertEquals((0.1), trainer1.getDiscount());
    }
    @Test
    public void editTrainerFail() {

        trainer1.setFirstName("abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc");
        trainer1.setLastName("def");
        trainer1.setEmail("abcdef@gmail.com");
        ((TrainerUser) trainer1).setIsPartner(false);


        assertThrows(RollbackException.class,()->um.updateUser(trainer1, trainer1.getUserId()));
    }

    @Test
    public void deleteAllUsers() {
        um.removeUser(student1.getUserId());
        um.removeUser(regular1.getUserId());
        um.removeUser(trainer1.getUserId());

        assertNull(um.findUsers(student1.getUserId()));
        assertNull(um.findUsers(regular1.getUserId()));
        assertNull(um.findUsers(trainer1.getUserId()));
    }
}
