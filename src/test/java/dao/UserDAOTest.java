package dao;

import config.DBConnection;
import dao.impl.UserDAOImpl;
import model.User;
import org.junit.jupiter.api.*;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {
    private static UserDAOImpl userDAO;
    private static Connection connection;
    private User testUser;

    @BeforeAll
    static void init() throws SQLException {
        connection = DBConnection.getConnection();
        userDAO = new UserDAOImpl(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        connection.setAutoCommit(false);

        testUser = User.builder()
                           .username("testuser_" + System.currentTimeMillis())
                           .passwordHash(PasswordUtil.hashPassword("123456"))
                           .fullName("Nguyen Test")
                           .createdAt(LocalDateTime.now())
                           .build();

        userDAO.insert(testUser);
    }

    @AfterEach
    void clear() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }

    @AfterAll
    static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @Order(1)
    void testInsertUser() {
        assertNotNull(testUser.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<User> found = userDAO.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(3)
    void testFindByUserName() {
        Optional<User> found = userDAO.findByUsername(testUser.getUsername());
        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
    }

    @Test
    @Order(4)
    void testFindByFullName() {
        Optional<User> found = userDAO.findByFullName("Nguyen Test");
        assertTrue(found.isPresent());
        assertTrue(found.get().getFullName().contains("Nguyen Test"));
    }

    @Test
    @Order(5)
    void testUpdateUser() {
        testUser.setFullName("Nguyen Updated");
        userDAO.update(testUser);

        User updated = userDAO.findById(testUser.getId()).orElseThrow();
        assertEquals("Nguyen Updated", updated.getFullName());
    }

    @Test
    @Order(6)
    void testFindAllUser() {
        assertFalse(userDAO.findAll().isEmpty());
    }

    @Test
    @Order(7)
    void testDeleteUser() {
        userDAO.delete(testUser.getId());
        Optional<User> deleted = userDAO.findById(testUser.getId());
        assertFalse(deleted.isPresent());
    }
}
