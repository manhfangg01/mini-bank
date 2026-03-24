package service.impl;

import config.DBConnection;
import dao.UserDAO;
import dao.impl.UserDAOImpl;
import model.User;
import service.UserService;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private User currentUser;

    @Override
    public void register(String username, String password, String fullName) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            UserDAO userDAO = new UserDAOImpl(conn);

            if (userDAO.findByUsername(username).isPresent()) {
                throw new RuntimeException("Username is already used!");
            }

            User newUser = User.builder()
                                   .username(username)
                                   .passwordHash(PasswordUtil.hashPassword(password))
                                   .fullName(fullName)
                                   .createdAt(LocalDateTime.now())
                                   .build();
            // save user
            userDAO.insert(newUser);

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Error during rollback", ex);
            }
            throw new RuntimeException("Register failed: " + e.getMessage());
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public String login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(conn);
            Optional<User> userOpt = userDAO.findByUsername(username);

            if (userOpt.isPresent() && PasswordUtil.checkPassword(password, userOpt.get().getPasswordHash())) {
                this.currentUser = userOpt.get();
                return "SUCCESS";
            }
            return "FAILED";
        } catch (SQLException e) {
            throw new RuntimeException("Error during login", e);
        }
    }

    @Override
    public void logout() {
        this.currentUser = null;
        System.out.println("Log out successfully!");
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) throw new RuntimeException("You're not logged in!");

        try (Connection conn = DBConnection.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(conn);

            if (!PasswordUtil.checkPassword(oldPassword, currentUser.getPasswordHash())) {
                throw new RuntimeException("Previous password is incorrect!");
            }

            currentUser.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            userDAO.update(currentUser);
            System.out.println("Password changed successfully!");

        } catch (SQLException e) {
            throw new RuntimeException("Error during password change", e);
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection", e);
            }
        }
    }
}
