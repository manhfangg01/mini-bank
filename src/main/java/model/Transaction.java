package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Transaction() {
    }

    public Transaction(String id, Integer senderAccountId, Integer receiverAccountId,
                       BigDecimal amount, BigDecimal fee, String type) {
        this.id = id;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(Integer senderAccountId) { this.senderAccountId = senderAccountId; }

    public Integer getReceiverAccountId() { return receiverAccountId; }
    public void setReceiverAccountId(Integer receiverAccountId) { this.receiverAccountId = receiverAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Transaction{" +
                       "id='" + id + '\'' +
                       ", type='" + type + '\'' +
                       ", amount=" + amount +
                       ", status='" + status + '\'' +
                       ", createdAt=" + createdAt +
                       '}';
    }
}