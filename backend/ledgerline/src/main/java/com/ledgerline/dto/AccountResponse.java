
package com.ledgerline.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        String holderName,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt
) {
}
