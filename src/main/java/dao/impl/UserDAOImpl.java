package dao.impl;

import dao.UserDAO;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private final Connection conn;

    public UserDAOImpl(Connection connection) {
        this.conn =connection;
    }

    @Override
    public User insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, created_at) VALUES (?, ?, ?, ?)";

        try( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Database error during insert", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by username", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByFullName(String fullName) {
        String sql = "SELECT * FROM users WHERE full_name LIKE ?";

        try( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, "%" + fullName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by fullName", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement st = conn.createStatement()){
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query *", e);
        }
        return list;
    }

    @Override
    public void update(User updatedUser)  {
        String sql = "UPDATE users SET full_name = ?, password_hash = ? WHERE id = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, updatedUser.getFullName());
            ps.setString(2, updatedUser.getPasswordHash());
            ps.setInt(3, updatedUser.getId());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during update", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error during delete", e);
        }
    }


    private User mapUser(ResultSet rs) throws SQLException {
       return User.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .fullName(rs.getString("full_name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}