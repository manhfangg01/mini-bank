package dao.impl;

import dao.AccountDAO;
import model.Account;
import util.constant.AccountStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAOImpl implements AccountDAO {
    private final Connection conn;

    public AccountDAOImpl(Connection connection) {
        this.conn = connection;
    }

    @Override
    public Account insert(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_number, balance, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(account.getCreatedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }

            return account;

        } catch (SQLException e) {
            throw new RuntimeException("Database error during account insert", e);
        }
    }

    @Override
    public Optional<Account> findById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query account by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query account by user ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query by account number", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query all accounts", e);
        }
        return list;
    }

    @Override
    public List<Account> findByStatus(AccountStatus status) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAccount(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during query accounts by status", e);
        }
        return list;
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE accounts SET balance = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, account.getBalance());
            ps.setString(2, account.getStatus().name());
            ps.setInt(3, account.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating account failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during account update", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error during account delete", e);
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        return Account.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .accountNumber(rs.getString("account_number"))
                        .balance(rs.getBigDecimal("balance"))
                        .status(AccountStatus.valueOf(rs.getString("status")))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
    }
}