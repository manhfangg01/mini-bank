package model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import util.constant.TransactionStatus;
import util.constant.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class Transaction {
    private String id; // UUID
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private TransactionType type; // TRANSFER, DEPOSIT, WITHDRAW
    private TransactionStatus status; // SUCCESS, FAILED
    private String message;
    private LocalDateTime createdAt;
}