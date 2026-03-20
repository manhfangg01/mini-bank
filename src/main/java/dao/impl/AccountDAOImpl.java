package dao.impl;

import config.DBConnection;
import dao.AccountDAO;
import model.Account;
import util.constant.AccountStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public void create(Account account) {
        if (findByUserId(account.getUserId()).isPresent()) {
            System.out.println(">> User already has an account: userId=" + account.getUserId());
            return;
        }

        if (findByAccountNumber(account.getAccountNumber()).isPresent()) {
            System.out.println(">> Account number already exists: " + account.getAccountNumber());
            return;
        }


        String sql = "INSERT INTO accounts (user_id, account_number, balance, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }

            System.out.println(">> Created Account: " + account.getAccountNumber());
        } catch (SQLException e) {
            System.err.println(">> Error when creating account: " + e.getMessage());
        }
    }

    @Override
    public Optional<Account> findById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(">> Error finding account by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(">> Error finding account by user ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(">> Error finding account by number: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println(">> Error finding all accounts: " + e.getMessage());
        }
        return accounts;
    }

    @Override
    public List<Account> findByStatus(AccountStatus status) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(">> Error finding accounts by status: " + e.getMessage());
        }
        return accounts;
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE accounts SET balance = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, account.getBalance());
            ps.setString(2, account.getStatus().name());
            ps.setInt(3, account.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(">> Updated Account: " + account.getAccountNumber());
            }
        } catch (SQLException e) {
            System.err.println(">> Error updating account: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(">> Deleted Account with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println(">> Error deleting account: " + e.getMessage());
        }
    }

    /**
     * Helper method to map a ResultSet row to an Account object
     */
    private Account mapAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(AccountStatus.valueOf(rs.getString("status")));
        return account;
    }
}