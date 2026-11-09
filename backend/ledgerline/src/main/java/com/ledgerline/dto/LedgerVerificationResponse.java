package com.ledgerline.dto;

import java.math.BigDecimal;

public class LedgerVerificationResponse {

    private Long transferId;
    private boolean balanced;
    private int entryCount;
    private BigDecimal debitTotal;
    private BigDecimal creditTotal;
    private String message;

    public LedgerVerificationResponse(
            Long transferId,
            boolean balanced,
            int entryCount,
            BigDecimal debitTotal,
            BigDecimal creditTotal,
            String message) {

        this.transferId = transferId;
        this.balanced = balanced;
        this.entryCount = entryCount;
        this.debitTotal = debitTotal;
        this.creditTotal = creditTotal;
        this.message = message;
    }

    public Long getTransferId() {
        return transferId;
    }

    public boolean isBalanced() {
        return balanced;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public BigDecimal getDebitTotal() {
        return debitTotal;
    }

    public BigDecimal getCreditTotal() {
        return creditTotal;
    }

    public String getMessage() {
        return message;
    }
}