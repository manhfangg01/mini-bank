package model;

import lombok.Builder;
import lombok.Data;
import util.constant.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Account {

    private Integer id;
    private Integer userId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private BigDecimal dailyTransferLimit;
    private BigDecimal dailyTransferUsed;
    private LocalDateTime createdAt;
}
