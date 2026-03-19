package dao.impl;

import config.DBConnection;
import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public void create(User user) {
        if(findByUsername(user.getUsername()).isPresent()) {
            System.out.println(">> Username already exists: " + user.getUsername());
            return;
        }


        String sql = "INSERT INTO users (username, password_hash, full_name, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            user.setPasswordHash("");
                ps.setString(1, user.getUsername());
                ps.setString(2, PasswordUtil.hashPassword( user.getPasswordHash()));
                ps.setString(3, user.getFullName());
                ps.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));

                ps.executeUpdate();


                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }

                System.out.println(">> Created User: " + user.getUsername());
        } catch (SQLException e) {
            System.err.println(">> Error when creating user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println(">> Error when finding user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println(">> Error when finding user by username: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByFullName(String fullName) {
        String sql = "SELECT * FROM users WHERE full_name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + fullName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println(">> Error when finding user by full name: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println(">> Error when finding all users: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(User updatedUser)  {
        String sql = "UPDATE users SET full_name = ?, password_hash = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updatedUser.getFullName());
            ps.setString(2, updatedUser.getPasswordHash());
            ps.setInt(3, updatedUser.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(">> Error when updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(">> Error when deleting user: " + e.getMessage());
        }
    }


    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}