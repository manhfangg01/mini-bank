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

    private Account senderAcc;
    private Account receiverAcc;
    private Transaction testTransaction;

    @BeforeAll
    static void init() throws SQLException {
        connection = DBConnection.getConnection();
        transactionDAO = new TransactionDAOImpl(connection);
        accountDAO = new AccountDAOImpl(connection);
        userDAO = new UserDAOImpl(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        connection.setAutoCommit(false);

        User senderUser = User.builder()
                                  .username("sender_" + System.currentTimeMillis())
                                  .passwordHash(PasswordUtil.hashPassword("123456"))
                                  .fullName("Sender User")
                                  .createdAt(LocalDateTime.now())
                                  .build();
        userDAO.insert(senderUser);

        User receiverUser = User.builder()
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
        transactionDAO.insert(testTransaction);
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
    void testInsertTransaction() {
        assertNotNull(testTransaction.getId());
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
        assertTrue(transactions.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    @Test
    @Order(4)
    void testFindByReceiverId() {
        List<Transaction> transactions = transactionDAO.findByReceiverId(receiverAcc.getId());
        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
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
        List<Transaction> transferTxs = transactionDAO.findByType(TransactionType.TRANSFER);
        assertTrue(transferTxs.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    @Test
    @Order(7)
    void testFindByMessage() {
        List<Transaction> list = transactionDAO.findByMessage("user 1");
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(8)
    void testUpdateTransaction() {
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
}
