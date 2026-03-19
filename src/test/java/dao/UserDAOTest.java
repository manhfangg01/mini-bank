package dao;

import dao.impl.UserDAOImpl;
import model.User;
import org.junit.jupiter.api.*;
import util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {
    private static UserDAOImpl userDAO;
    private static User testUser;

    @BeforeAll
    static void setup() {
        userDAO = new UserDAOImpl();
        testUser = new User();
        testUser.setUsername("testuser_" + System.currentTimeMillis());
        testUser.setPasswordHash(PasswordUtil.hashPassword("123456"));
        testUser.setFullName("Nguyen Test");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @Order(1)
    void testCreateUser() {
        userDAO.create(testUser);
        assertNotNull(testUser.getId(), "ID should be set after creation");
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<User> found = userDAO.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
    }

    @Test
    @Order(3)
    void testFindByUserName() {
        Optional<User> found = userDAO.findByUsername(testUser.getUsername());
        assertTrue(found.isPresent());
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(4)
    void testFindByFullName() {
        Optional<User> found = userDAO.findByFullName(testUser.getFullName());
        assertTrue(found.isPresent());
        assertTrue(found.get().getFullName().contains(testUser.getFullName()));
    }


    @Test
    @Order(5)
    void testUpdateUser() {
        testUser.setFullName("Nguyen Updated");
        userDAO.update(testUser);

        User updated = userDAO.findById(testUser.getId()).get();
        assertEquals("Nguyen Updated", updated.getFullName());
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
        assertFalse(deleted.isPresent(), "User should be deleted");
    }
}
