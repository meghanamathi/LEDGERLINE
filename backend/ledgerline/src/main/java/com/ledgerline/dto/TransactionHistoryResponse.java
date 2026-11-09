package com.ledgerline.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryResponse {

    private Long transactionId;
    private String transactionType;
    private String direction;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private String description;

    public TransactionHistoryResponse() {}

    public TransactionHistoryResponse(
            Long transactionId,
            String transactionType,
            String direction,
            BigDecimal amount,
            String currency,
            String status,
            LocalDateTime createdAt,
            String description) {

        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.direction = direction;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.description = description;
    }

    public Long getTransactionId() { return transactionId; }
    public String getTransactionType() { return transactionType; }
    public String getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getDescription() { return description; }
}