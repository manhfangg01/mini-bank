package service.impl;

import config.DBConnection;
import dao.AccountDAO;
import dao.impl.AccountDAOImpl;
import model.Account;
import model.User;
import model.UserSession;
import service.AccountService;
import util.constant.AccountStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    @Override
    public void openAccount(Integer userId, AccountStatus initialStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAOImpl(conn);

            Account newAcc = Account.builder()
                                     .userId(userId)
                                     .accountNumber("ACC" + System.currentTimeMillis())
                                     .balance(BigDecimal.ZERO)
                                     .status(initialStatus)
                                     .createdAt(LocalDateTime.now())
                                     .build();

            accountDAO.insert(newAcc);
            System.out.println("Create account successful with: " + newAcc.getAccountNumber());
        } catch (SQLException e) {
            throw new RuntimeException("Error during open account", e);
        }
    }

    @Override
    public BigDecimal getBalance(Integer accountId) {
        if (!UserSession.isLoggedIn()) {
            throw new RuntimeException("You must be logged in to view account balance!");
        }
        User currentUser = UserSession.getCurrentUser();


        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAOImpl(conn);

            Optional<Account> accountOpt = accountDAO.findById(accountId);

            if (accountOpt.isEmpty()) {
                throw new RuntimeException("Account is not existed!");
            }

            Account account = accountOpt.get();

            if (!account.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only view balance of your own accounts!");
            }

            return account.getBalance();

        } catch (SQLException e) {
            throw new RuntimeException("Error during get balance", e);
        }
    }

    @Override
    public List<Account> getMyAccounts() {
        if (!UserSession.isLoggedIn()) {
            throw new RuntimeException("You must be logged in to view account balance!");
        }
        User currentUser = UserSession.getCurrentUser();
        try(Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAOImpl(conn);
            return accountDAO.findByUserId(currentUser.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error during get my accounts", e);
        }
    }

    @Override
    public Account getAccountByNumber(String accountNumber) {
        if (!UserSession.isLoggedIn()) {
            throw new RuntimeException("You must be logged in to view account!");
        }
        User currentUser = UserSession.getCurrentUser();


        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAOImpl(conn);

            Optional<Account> accountOpt = accountDAO.findByAccountNumber(accountNumber);

            if (accountOpt.isEmpty()) {
                throw new RuntimeException("Account is not existed!");
            }

            Account account = accountOpt.get();

            if (!account.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only view balance of your own accounts!");
            }

            return account;

        } catch (SQLException e) {
            throw new RuntimeException("Error during get account by number", e);
        }
    }

    @Override
    public void changeAccountStatus(Integer accountId, AccountStatus newStatus) {
        if (!UserSession.isLoggedIn()) {
            throw new RuntimeException("You must be logged in to view account balance!");
        }
        User currentUser = UserSession.getCurrentUser();
        try (Connection conn = DBConnection.getConnection()) {
            AccountDAO accountDAO = new AccountDAOImpl(conn);

            Optional<Account> accountOpt = accountDAO.findById(accountId);

            if (accountOpt.isEmpty()) {
                throw new RuntimeException("Account is not existed!");
            }

            Account account = accountOpt.get();

            if (!account.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only change status if you're the card owner!");
            }

            account.setStatus(newStatus);
            accountDAO.update(account);

        } catch (SQLException e) {
            throw new RuntimeException("Error during get account by number", e);
        }
    }
}
