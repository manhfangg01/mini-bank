package dao;

import model.Transaction;
import util.constant.TransactionStatus;
import util.constant.TransactionType;

import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction insert(Transaction transaction);
    Optional<Transaction> findById(String id);
    List<Transaction> findBySenderId(int accountId);
    List<Transaction> findByReceiverId(int accountId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByMessage(String Message);
    List<Transaction> findAll();
     void update(Transaction transaction);
     void delete(String id);
}
