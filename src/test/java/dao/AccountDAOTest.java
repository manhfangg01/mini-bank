package dao;

import dao.impl.AccountDAOImpl;
import dao.impl.UserDAOImpl;
import model.Account;
import model.User;
import org.junit.jupiter.api.*;
import util.PasswordUtil;
import util.constant.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDAOTest {

    private static AccountDAOImpl accountDAO;
    private static UserDAOImpl userDAO;
    private static Account testAccount;
    private static User owner;

    @BeforeAll
    static void setup() {
        accountDAO = new AccountDAOImpl();
        userDAO = new UserDAOImpl();

        owner = new User();
        owner.setUsername("owner_" + System.currentTimeMillis());
        owner.setPasswordHash(PasswordUtil.hashPassword("123456"));
        owner.setFullName("Account Owner");
        owner.setCreatedAt(LocalDateTime.now());
        userDAO.create(owner);

        testAccount = new Account();
        testAccount.setUserId(owner.getId());
        testAccount.setAccountNumber("ACC" + System.currentTimeMillis());
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    @Order(1)
    void testCreateAccount() {
        accountDAO.create(testAccount);
        assertNotNull(testAccount.getId(), "Account ID should be set after creation");
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
        Optional<Account> found = accountDAO.findByUserId(owner.getId());
        assertTrue(found.isPresent());
        assertEquals(owner.getId(), found.get().getUserId());
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
        List<Account> activeAccounts = accountDAO.findByStatus(AccountStatus.ACTIVE);
        assertFalse(activeAccounts.isEmpty());
        assertTrue(activeAccounts.stream().anyMatch(acc -> Objects.equals(acc.getId(), testAccount.getId())));
    }

    @Test
    @Order(6)
    void testUpdateAccount() {
        testAccount.setBalance(new BigDecimal("5000.50"));
        testAccount.setStatus(AccountStatus.LOCKED);
        accountDAO.update(testAccount);

        assertTrue(accountDAO.findById(testAccount.getId()).isPresent(), "Updated account should still exist");
        Account updated = accountDAO.findById(testAccount.getId()).get();
        assertEquals(0, new BigDecimal("5000.50").compareTo(updated.getBalance()));
        assertEquals(AccountStatus.LOCKED, updated.getStatus());
    }

    @Test
    @Order(7)
    void testFindAll() {
        List<Account> accounts = accountDAO.findAll();
        assertFalse(accounts.isEmpty(), "Account list should not be empty");
    }

    @Test
    @Order(8)
    void testDeleteAccount() {
        accountDAO.delete(testAccount.getId());
        Optional<Account> deleted = accountDAO.findById(testAccount.getId());
        assertFalse(deleted.isPresent(), "Account should be deleted");
    }

    @AfterAll
    static void tearDown() {
        if (owner != null && owner.getId() > 0) {
            userDAO.delete(owner.getId());
        }
    }
}
