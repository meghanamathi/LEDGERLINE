
package com.ledgerline.controller;

import com.ledgerline.dto.DepositRequest;
import com.ledgerline.entity.Deposit;
import com.ledgerline.service.DepositService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createDeposit(
            @Valid @RequestBody DepositRequest request) {

        Deposit deposit = depositService.depositMoney(request);

        return Map.of(
            "depositId", deposit.getId(),
            "accountNumber", deposit.getAccount().getAccountNumber(),
            "amount", deposit.getAmount(),
            "currency", deposit.getCurrency(),
            "status", deposit.getStatus(),
            "message", "Deposit completed successfully"
        );
    }
}
