package org.padan.Model.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Manager.UserManager;
import org.padan.Model.Objects.*;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    UserRepository repository;
    private ClientSession session;
    private UserManager userManager;

    User testUser;
    ObjectId testId;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
        session = repository.startSession();
        userManager = new UserManager(repository, repository.getMongoClient());

        try {
            for (User u : repository.findAll()) {
                userManager.removeUser(u.getUserId());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        testId = new ObjectId("100010000000000000000000");

        testUser = new StudentUser("Test", "Student", "ts@gmail.com");
        testUser.setUserId(testId);
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            try {
                userManager.removeUser(testId);
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
                for (User u : repository.findAll()) {
                    userManager.removeUser(u.getUserId());
                }
                repository.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    void addUser() {
        assertEquals(0, repository.findAll().size());

        userManager.registerUser(testUser);

        assertEquals(1, repository.findAll().size());


        ObjectId newId = new ObjectId("111000000000000000000000");
        User tu = new StudentUser("Tester", "StudentTester", "test@gmail.com");
        tu.setUserId(newId);

        userManager.registerUser(tu);

        assertEquals(2, repository.findAll().size());
        assertEquals(tu.getUserId(), repository.findById(tu.getUserId()).getUserId());
        assertEquals(tu.getFirstName(), repository.findById(tu.getUserId()).getFirstName());
        assertEquals(tu.getLastName(), repository.findById(tu.getUserId()).getLastName());
        assertEquals(tu.getEmail(), repository.findById(tu.getUserId()).getEmail());
    }

    @Test
    void updateUser() {
        userManager.registerUser(testUser);

        testUser.setFirstName("Tester");
        userManager.updateUser(testUser.getUserId(), testUser);
        assertEquals(testUser.getFirstName(), repository.findById(testUser.getUserId()).getFirstName());
    }

    @Test
    void deleteUser() {
        assertEquals(0, repository.findAll().size());

        userManager.registerUser(testUser);
        assertEquals(1, repository.findAll().size());

        userManager.removeUser(testId);
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void fromPojoTest() {
        userManager.registerUser(testUser);

        MongoCollection<Document> raw = repository.getRentAFieldDB().getCollection("users", Document.class);
        Document doc = raw.find(eq("_id", testId)).first();

        assertNotNull(doc);
        assertEquals("Test", doc.getString("first_name"));
        assertEquals("Student", doc.getString("last_name"));
        assertEquals("ts@gmail.com", doc.getString("email"));
        assertEquals("student", doc.getString("clazz"));
    }

    @Test
    void toPojoTest() {
        userManager.registerUser(testUser);
        User fromDb = repository.findById(testId);

        assertNotNull(fromDb);
        assertInstanceOf(StudentUser.class, fromDb);
        assertEquals("Test", fromDb.getFirstName());
        assertEquals("Student", fromDb.getLastName());
        assertEquals("ts@gmail.com", fromDb.getEmail());
    }

    @Test
    void differetUserTypesTest() {
        ObjectId regularId = new ObjectId("111110000000000000000000");
        ObjectId trainerId = new ObjectId("111110000000000000000001");

        User regularUser = new RegularUser("Regular", "User", "regular@gmail.com");
        regularUser.setUserId(regularId);

        User trainerUser = new TrainerUser("Trainer", "User", "trainer@gmail.com", true);
        trainerUser.setUserId(trainerId);

        userManager.registerUser(regularUser);
        userManager.registerUser(trainerUser);

        MongoCollection<Document> raw = repository.getRentAFieldDB().getCollection("users", Document.class);

        Document regDoc = raw.find(eq("_id", regularId)).first();
        assertNotNull(regDoc);
        assertEquals("Regular", regDoc.getString("first_name"));
        assertEquals("User", regDoc.getString("last_name"));
        assertEquals("regular@gmail.com", regDoc.getString("email"));
        assertEquals("regular", regDoc.getString("clazz"));

        Document trainerDoc = raw.find(eq("_id", trainerId)).first();
        assertNotNull(trainerDoc);
        assertEquals("Trainer", trainerDoc.getString("first_name"));
        assertEquals("User", trainerDoc.getString("last_name"));
        assertEquals("trainer@gmail.com", trainerDoc.getString("email"));
        assertEquals("trainer", trainerDoc.getString("clazz"));
    }

}