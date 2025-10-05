package org.padan.Model.Objects;

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
        assertEquals(0.25,student1.getDiscount());
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
}
