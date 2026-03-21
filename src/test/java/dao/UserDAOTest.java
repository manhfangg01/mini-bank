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
    private static User testUser;
    private static Connection connection;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DBConnection.getConnection();
        userDAO = new UserDAOImpl(connection);

        testUser = User.builder()
                           .username("testuser_" + System.currentTimeMillis())
                           .passwordHash(PasswordUtil.hashPassword("123456"))
                           .fullName("Nguyen Test")
                           .createdAt(LocalDateTime.now())
                           .build();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
             connection.close();
        }
    }

    @Test
    @Order(1)
    void testCreateUser() {
        User savedUser = userDAO.insert(testUser);

        assertNotNull(savedUser.getId(), "ID should be set after creation");
        testUser.setId(savedUser.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<User> found = userDAO.findById(testUser.getId());
        assertTrue(found.isPresent(), "User should be found by ID");
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(3)
    void testFindByUserName() {
        Optional<User> found = userDAO.findByUsername(testUser.getUsername());
        assertTrue(found.isPresent(), "User should be found by username");
        assertEquals(testUser.getId(), found.get().getId());
    }

    @Test
    @Order(4)
    void testFindByFullName() {
        Optional<User> found = userDAO.findByFullName("Nguyen Test");
        assertTrue(found.isPresent(), "User should be found by full name");
        assertTrue(found.get().getFullName().contains("Nguyen Test"));
    }

    @Test
    @Order(5)
    void testUpdateUser() {
        testUser.setFullName("Nguyen Updated");
        userDAO.update(testUser);

        Optional<User> updatedOpt = userDAO.findById(testUser.getId());
        assertTrue(updatedOpt.isPresent());
        assertEquals("Nguyen Updated", updatedOpt.get().getFullName());
    }

    @Test
    @Order(6)
    void testFindAllUser() {
        assertFalse(userDAO.findAll().isEmpty(), "User list should not be empty");
    }

    @Test
    @Order(7)
    void testDeleteUser() {
        userDAO.delete(testUser.getId());
        Optional<User> deleted = userDAO.findById(testUser.getId());
        assertFalse(deleted.isPresent(), "User should be deleted from database");
    }
}
