package model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Transaction {
    private String id; // UUID
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private String type; // TRANSFER, DEPOSIT, WITHDRAW
    private String status; // SUCCESS, FAILED
    private String message;
    private LocalDateTime createdAt;


}