package dao.impl;

import model.Transaction;
import util.constant.TransactionStatus;
import util.constant.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements dao.TransactionDAO {
    private final Connection conn;

    public TransactionDAOImpl(Connection connection) {
        this.conn = connection;
    }

    @Override
    public Transaction insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (id, sender_account_id, receiver_account_id, amount, type, status, message, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transaction.getId());

            if (transaction.getSenderAccountId() != null) ps.setInt(2, transaction.getSenderAccountId());
            else ps.setNull(2, Types.INTEGER); // still add null in the query with setNull if needed

            if (transaction.getReceiverAccountId() != null) ps.setInt(3, transaction.getReceiverAccountId());
            else ps.setNull(3, Types.INTEGER);

            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getType().name());
            ps.setString(6, transaction.getStatus().name());
            ps.setString(7, transaction.getMessage());
            ps.setTimestamp(8, Timestamp.valueOf(transaction.getCreatedAt()));

            ps.executeUpdate();

            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transaction insert", e);
        }
    }

    @Override
    public Optional<Transaction> findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query transaction by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findBySenderId(int accountId) {
        return findByColumn("sender_account_id", accountId);
    }

    @Override
    public List<Transaction> findByReceiverId(int accountId) {
        return findByColumn("receiver_account_id", accountId);
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        String sql = "SELECT * FROM transactions WHERE status = ?";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by status", e);
        }
        return list;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        String sql = "SELECT * FROM transactions WHERE type = ?";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by type", e);
        }
        return list;
    }

    @Override
    public List<Transaction> findByMessage(String message) {
        String sql = "SELECT * FROM transactions WHERE message LIKE ?";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + message + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by message", e);
        }
        return list;
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions";
        List<Transaction> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query all transactions", e);
        }
        return list;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET status = ?, message = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaction.getStatus().name());
            ps.setString(2, transaction.getMessage());
            ps.setString(3, transaction.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transaction update", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transaction delete", e);
        }
    }


    private List<Transaction> findByColumn(String column, int value) {
        String sql = "SELECT * FROM transactions WHERE " + column + " = ?";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by " + column, e);
        }
        return list;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        BigDecimal amount = rs.getBigDecimal("amount");
        String message = rs.getString("message");

        int sId = rs.getInt("sender_account_id");
        Integer senderId = rs.wasNull() ? null : sId;

        int rId = rs.getInt("receiver_account_id");
        Integer receiverId = rs.wasNull() ? null : rId;

        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        TransactionStatus status = TransactionStatus.valueOf(rs.getString("status"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return Transaction.builder()
                       .id(id)
                       .senderAccountId(senderId)
                       .receiverAccountId(receiverId)
                       .amount(amount)
                       .type(type)
                       .status(status)
                       .message(message)
                       .createdAt(createdAt)
                       .build();
    }
}
