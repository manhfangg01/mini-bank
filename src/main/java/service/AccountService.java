package service;

import model.Account;
import util.constant.AccountStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    void openAccount(Integer userId, AccountStatus initialStatus);

    BigDecimal getBalance(Integer accountId);

    List<Account> getMyAccounts();

    Account getAccountByNumber(String accountNumber);

    void changeAccountStatus(Integer accountId, AccountStatus newStatus);

}
