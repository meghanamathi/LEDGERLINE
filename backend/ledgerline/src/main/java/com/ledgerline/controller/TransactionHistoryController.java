
package com.ledgerline.controller;

import com.ledgerline.dto.TransactionHistoryResponse;
import com.ledgerline.service.TransactionHistoryService;
import com.ledgerline.repository.AccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class TransactionHistoryController {

    private final TransactionHistoryService historyService;
    private final AccountRepository accountRepository;

    public TransactionHistoryController(
            TransactionHistoryService historyService,
            AccountRepository accountRepository) {
        this.historyService = historyService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable String accountNumber) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isOwner = accountRepository
                .findByAccountNumber(accountNumber)
                .map(account -> account.getOwner() != null
                        && account.getOwner().getEmail().equals(email))
                .orElse(false);

        if (!isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account not found"
            );
        }

        return historyService.getTransactionHistory(accountNumber);
    }
}
