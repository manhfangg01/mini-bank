package dao;

import model.Account;
import util.constant.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountDAO {
        Account insert(Account account);
        Optional<Account> findById(int id);
        List<Account> findByUserId(int userId);
        Optional<Account> findByAccountNumber(String accountNumber);
        List<Account> findAll();
        List<Account> findByStatus(AccountStatus status);
        void update(Account account);
        void delete(int id);
        void deleteByUserId(int userId);
}
