package org.padan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.padan.Model.Manager.ReservationManager;
import org.padan.Model.Manager.RoomManager;
import org.padan.Model.Manager.UserManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
public class BaseTC {

    @Container
    private static PostgreSQLContainer postgresqlContainer = (PostgreSQLContainer) new
            PostgreSQLContainer(DockerImageName.parse("postgres:17"))
            .withDatabaseName("nbddb")
            .withUsername("nbd")
            .withPassword("nbdpassword")
            .withExposedPorts(5432);

    protected static EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction transaction;
    protected ReservationManager resm;
    protected UserManager um;
    protected RoomManager rm;

    @BeforeAll
    public static void startDB(){
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", postgresqlContainer.getJdbcUrl());
        properties.put("javax.persistence.jdbc.user", postgresqlContainer.getUsername());
        properties.put("javax.persistence.jdbc.password", postgresqlContainer.getPassword());
        emf = Persistence.createEntityManagerFactory("nbddb", properties);
    }

    @BeforeEach
    public void setupDB() {
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        resm = new ReservationManager(em, transaction);
        um = new UserManager(em, transaction);
        rm = new RoomManager(em, transaction);
    }

}
