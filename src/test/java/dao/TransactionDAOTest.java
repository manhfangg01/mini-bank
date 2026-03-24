package dao;

import config.DBConnection;
import dao.impl.AccountDAOImpl;
import dao.impl.TransactionDAOImpl;
import dao.impl.UserDAOImpl;
import model.Account;
import model.Transaction;
import model.User;
import org.junit.jupiter.api.*;
import util.PasswordUtil;
import util.UUIDUtil;
import util.constant.AccountStatus;
import util.constant.TransactionStatus;
import util.constant.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionDAOTest {

    private static TransactionDAOImpl transactionDAO;
    private static AccountDAOImpl accountDAO;
    private static UserDAOImpl userDAO;

    private static Connection connection;
    private static User senderUser;
    private static User receiverUser;
    private static Account senderAcc;
    private static Account receiverAcc;
    private static Transaction testTransaction;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DBConnection.getConnection();
        transactionDAO = new TransactionDAOImpl(connection);
        accountDAO = new AccountDAOImpl(connection);
        userDAO = new UserDAOImpl(connection);

        senderUser = User.builder()
                             .username("sender_" + System.currentTimeMillis())
                             .passwordHash(PasswordUtil.hashPassword("123456"))
                             .fullName("Sender User")
                             .createdAt(LocalDateTime.now())
                             .build();
        userDAO.insert(senderUser);

        receiverUser = User.builder()
                               .username("receiver_" + System.currentTimeMillis())
                               .passwordHash(PasswordUtil.hashPassword("123456"))
                               .fullName("Receiver User")
                               .createdAt(LocalDateTime.now())
                               .build();
        userDAO.insert(receiverUser);

        senderAcc = Account.builder()
                            .userId(senderUser.getId())
                            .accountNumber("SND" + System.currentTimeMillis())
                            .balance(new BigDecimal("5000.00"))
                            .status(AccountStatus.ACTIVE)
                            .createdAt(LocalDateTime.now())
                            .build();
        accountDAO.insert(senderAcc);

        receiverAcc = Account.builder()
                              .userId(receiverUser.getId())
                              .accountNumber("RCV" + System.currentTimeMillis())
                              .balance(new BigDecimal("1000.00"))
                              .status(AccountStatus.ACTIVE)
                              .createdAt(LocalDateTime.now())
                              .build();
        accountDAO.insert(receiverAcc);

        testTransaction = Transaction.builder()
                                  .id(UUIDUtil.generate())
                                  .senderAccountId(senderAcc.getId())
                                  .receiverAccountId(receiverAcc.getId())
                                  .amount(new BigDecimal("500.00"))
                                  .type(TransactionType.TRANSFER)
                                  .status(TransactionStatus.PENDING)
                                  .message("transfer from user 1 to user 2")
                                  .createdAt(LocalDateTime.now())
                                  .build();
    }

    @Test
    @Order(1)
    void testInsertTransaction() {
        Transaction result = transactionDAO.insert(testTransaction);
        assertNotNull(result.getId());
        assertEquals(testTransaction.getId(), result.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<Transaction> found = transactionDAO.findById(testTransaction.getId());
        assertTrue(found.isPresent());
        assertEquals(0, testTransaction.getAmount().compareTo(found.get().getAmount()));
    }

    @Test
    @Order(3)
    void testFindBySenderId() {
        List<Transaction> transactions = transactionDAO.findBySenderId(senderAcc.getId());
        assertFalse(transactions.isEmpty());
        assertEquals(testTransaction.getId(), transactions.getFirst().getId());
    }

    @Test
    @Order(4)
    void testFindByReceiverId() {
        List<Transaction> transactions = transactionDAO.findByReceiverId(receiverAcc.getId());
        assertFalse(transactions.isEmpty());
        assertEquals(testTransaction.getId(), transactions.getFirst().getId());
    }

    @Test
    @Order(5)
    void testFindByStatus() {
        List<Transaction> pendingTxs = transactionDAO.findByStatus(TransactionStatus.PENDING);
        assertTrue(pendingTxs.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    @Test
    @Order(6)
    void testFindByType() {
        List<Transaction> pendingTxs = transactionDAO.findByType(TransactionType.TRANSFER);
        assertTrue(pendingTxs.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    @Test
    @Order(7)
    void testFindByMessage() {
        List<Transaction> list = transactionDAO.findByMessage("user 1");
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(8)
    void testUpdateTransactionStatus() {
        testTransaction.setStatus(TransactionStatus.SUCCESS);
        testTransaction.setMessage("complete transaction");
        transactionDAO.update(testTransaction);

        Transaction updated = transactionDAO.findById(testTransaction.getId()).orElseThrow();
        assertEquals(TransactionStatus.SUCCESS, updated.getStatus());
        assertEquals("complete transaction", updated.getMessage());
    }

    @Test
    @Order(9)
    void testFindAll() {
        List<Transaction> all = transactionDAO.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @Order(10)
    void testDeleteTransaction() {
        transactionDAO.delete(testTransaction.getId());
        Optional<Transaction> deleted = transactionDAO.findById(testTransaction.getId());
        assertFalse(deleted.isPresent());
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (senderAcc != null) accountDAO.delete(senderAcc.getId());
        if (receiverAcc != null) accountDAO.delete(receiverAcc.getId());
        if (senderUser != null) userDAO.delete(senderUser.getId());
        if (receiverUser != null) userDAO.delete(receiverUser.getId());

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}