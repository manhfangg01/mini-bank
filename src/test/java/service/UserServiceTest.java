package service;

import config.DBConnection;
import dao.UserDAO;
import dao.impl.UserDAOImpl;
import model.User;
import model.UserSession;
import org.junit.jupiter.api.*;
import service.impl.UserServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private static UserServiceImpl userService;
    private static UserDAO userDAO;
    private static Connection connection;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DBConnection.getConnection();
        userService = new UserServiceImpl();
        userDAO = new UserDAOImpl(connection);
    }


    void tidyUp(String username) throws SQLException {
        if (connection.isClosed()){
            connection = DBConnection.getConnection();
            userDAO = new UserDAOImpl(connection);
        }
        Optional<User> userOpt = userDAO.findByUsername(username);
        userOpt.ifPresent(user -> userDAO.delete(user.getId()));
        userService.logout();
    }

    // --- HAPPY PATHS ---

    @Test
    @Order(1)
    void testRegisterSuccess() throws SQLException {
        String username = "happy_user_" + System.currentTimeMillis();

        userService.register(username, "123456", "Happy User");
        if (connection.isClosed()){
            connection = DBConnection.getConnection();
            userDAO = new UserDAOImpl(connection);
        }
        Optional<User> userOpt = userDAO.findByUsername(username);
        assertTrue(userOpt.isPresent(), "User should be created in DB");

        tidyUp(username);
    }

    @Test
    @Order(2)
    void testLoginSuccess() throws SQLException {
        String username = "login_user_" + System.currentTimeMillis();
        userService.register(username, "123456", "Login User");

        if (connection.isClosed()){
            connection = DBConnection.getConnection();
            userDAO = new UserDAOImpl(connection);
        }

        String result = userService.login(username, "123456");

        assertEquals("SUCCESS", result);
        assertNotNull(UserSession.getCurrentUser());
        assertEquals(username, userService.getCurrentUser().get().getUsername());

        tidyUp(username);
    }

    @Test
    @Order(3)
    void testChangePasswordSuccess() throws SQLException {
        String username = "change_pass_user_" + System.currentTimeMillis();
        userService.register(username, "old_pass", "Change Pass User");

        userService.login(username, "old_pass");
        
        userService.changePassword("old_pass", "new_pass");
        
        // Verify password changed and user can login with new password
        userService.logout();
        assertEquals("SUCCESS", userService.login(username, "new_pass"));
        
        tidyUp(username);
    }

    // --- SAD PATHS ---

    @Test
    @Order(4)
    void testRegisterFailDueToDuplicateUsername() {
        String username = "duplicate_user_" + System.currentTimeMillis();

        userService.register(username, "pass", "First User");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(username, "new_pass", "Second User"));

        assertTrue(exception.getMessage().contains("Username is already used"),
                "Message should mention duplicate username");
                
        try { tidyUp(username); } catch (SQLException e) { e.printStackTrace(); }
    }

    @Test
    @Order(5)
    void testRegisterRollback() throws SQLException {
        String fullName = "rollback_user_" + System.currentTimeMillis();

        assertThrows(RuntimeException.class, () -> userService.register(null, "123456", fullName));

        try (Connection checkConn = DBConnection.getConnection()) {
            UserDAO checkUserDAO = new UserDAOImpl(checkConn);
            Optional<User> userOpt = checkUserDAO.findByFullName(fullName);
            assertFalse(userOpt.isPresent(), "User should NOT be in DB if account creation failed");
        }
    }

    @Test
    @Order(6)
    void testLoginFailWrongPassword() {
        String username = "wrong_pass_user"+System.currentTimeMillis();
        userService.register(username, "correct_pass", "User Test");

        String result = userService.login(username, "incorrect_pass");

        assertEquals("FAILED", result);
        assertFalse(userService.getCurrentUser().isPresent(), "Session should be empty");
        
        try { tidyUp(username); } catch (SQLException e) { e.printStackTrace(); }
    }

    @Test
    @Order(7)
    void testChangePasswordFail() {
        String username = "change_pass_user"+System.currentTimeMillis();
        userService.register(username, "old_pass", "User Test");
        userService.login(username, "old_pass");

        assertThrows(RuntimeException.class, () -> userService.changePassword("wrong_old_pass", "new_pass"));
        
        try { tidyUp(username); } catch (SQLException e) { e.printStackTrace(); }
    }
}
