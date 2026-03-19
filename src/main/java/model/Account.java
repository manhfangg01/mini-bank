package model;

import util.constant.AccountStatus;

import java.math.BigDecimal;

public class Account {

    private Integer id;
    private Integer userId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private BigDecimal dailyTransferLimit;
    private BigDecimal dailyTransferUsed;

    public Account() {
    }

    public Account(Integer id, Integer userId, String accountNumber, BigDecimal balance, AccountStatus status, BigDecimal dailyTransferLimit, BigDecimal dailyTransferUsed) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.dailyTransferLimit = dailyTransferLimit;
        this.dailyTransferUsed = dailyTransferUsed;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public BigDecimal getDailyTransferLimit() { return dailyTransferLimit; }
    public void setDailyTransferLimit(BigDecimal dailyTransferLimit) { this.dailyTransferLimit = dailyTransferLimit; }

    public BigDecimal getDailyTransferUsed() { return dailyTransferUsed; }
    public void setDailyTransferUsed(BigDecimal dailyTransferUsed) { this.dailyTransferUsed = dailyTransferUsed; }

    @Override
    public String toString() {
        return "Account{" +
                       "id=" + id +
                       ", userId=" + userId +
                       ", accountNumber='" + accountNumber + '\'' +
                       ", balance=" + balance +
                       ", status='" + status + '\'' +
                       ", dailyTransferLimit=" + dailyTransferLimit +
                       ", dailyTransferUsed=" + dailyTransferUsed +
                       '}';
    }
}
