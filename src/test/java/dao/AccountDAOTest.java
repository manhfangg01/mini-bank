package dao;

import config.DBConnection;
import dao.impl.AccountDAOImpl;
import dao.impl.UserDAOImpl;
import model.Account;
import model.User;
import org.junit.jupiter.api.*;
import util.PasswordUtil;
import util.constant.AccountStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDAOTest {

    private static AccountDAOImpl accountDAO;
    private static UserDAOImpl userDAO;
    private static Connection connection;

    private User owner;
    private Account testAccount;

    @BeforeAll
    static void init() throws SQLException {
        connection = DBConnection.getConnection();
        accountDAO = new AccountDAOImpl(connection);
        userDAO = new UserDAOImpl(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        connection.setAutoCommit(false);

        owner = User.builder()
                        .username("owner_" + System.currentTimeMillis())
                        .passwordHash(PasswordUtil.hashPassword("123456"))
                        .fullName("Account Owner")
                        .createdAt(LocalDateTime.now())
                        .build();
        userDAO.insert(owner);

        testAccount = Account.builder()
                              .userId(owner.getId())
                              .accountNumber("ACC" + System.currentTimeMillis())
                              .balance(new BigDecimal("1000.00"))
                              .status(AccountStatus.ACTIVE)
                              .createdAt(LocalDateTime.now())
                              .build();
        accountDAO.insert(testAccount);
    }

    @AfterEach
    void clear() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }

    @AfterAll
    static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @Order(1)
    void testInsertAccount() {
        assertNotNull(testAccount.getId());
        assertTrue(testAccount.getId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<Account> found = accountDAO.findById(testAccount.getId());
        assertTrue(found.isPresent());
        assertEquals(testAccount.getAccountNumber(), found.get().getAccountNumber());
    }

    @Test
    @Order(3)
    void testFindByUserId() {
        List<Account> accounts = accountDAO.findByUserId(owner.getId());
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals(testAccount.getId(), accounts.getFirst().getId());

        Account secondAccount = Account.builder()
                                        .userId(owner.getId())
                                        .accountNumber("ACC2_" + System.currentTimeMillis())
                                        .balance(BigDecimal.ZERO)
                                        .status(AccountStatus.ACTIVE)
                                        .createdAt(LocalDateTime.now())
                                        .build();
        accountDAO.insert(secondAccount);

        List<Account> updatedAccounts = accountDAO.findByUserId(owner.getId());
        assertTrue(updatedAccounts.size() >= 2);
    }

    @Test
    @Order(4)
    void testFindByAccountNumber() {
        Optional<Account> found = accountDAO.findByAccountNumber(testAccount.getAccountNumber());
        assertTrue(found.isPresent());
        assertEquals(testAccount.getId(), found.get().getId());
    }

    @Test
    @Order(5)
    void testFindByStatus() {
        List<Account> actives = accountDAO.findByStatus(AccountStatus.ACTIVE);
        assertFalse(actives.isEmpty());
        assertTrue(actives.stream().anyMatch(a -> Objects.equals(a.getId(), testAccount.getId())));

        List<Account> locked = accountDAO.findByStatus(AccountStatus.LOCKED);
        assertNotNull(locked);
    }

    @Test
    @Order(6)
    void testFindAll() {
        List<Account> all = accountDAO.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @Order(7)
    void testUpdateAccount() {
        BigDecimal newBalance = new BigDecimal("9999.99");
        testAccount.setBalance(newBalance);
        testAccount.setStatus(AccountStatus.LOCKED);

        accountDAO.update(testAccount);

        Account updated = accountDAO.findById(testAccount.getId()).orElseThrow();
        assertEquals(0, newBalance.compareTo(updated.getBalance()));
        assertEquals(AccountStatus.LOCKED, updated.getStatus());
    }

    @Test
    @Order(8)
    void testDeleteAccount() {
        accountDAO.delete(testAccount.getId());
        Optional<Account> found = accountDAO.findById(testAccount.getId());
        assertFalse(found.isPresent());
    }

    @Test
    @Order(9)
    void testDeleteByUserId() {
        accountDAO.deleteByUserId(owner.getId());
        List<Account> accounts = accountDAO.findByUserId(owner.getId());
        assertTrue(accounts.isEmpty());
    }
}