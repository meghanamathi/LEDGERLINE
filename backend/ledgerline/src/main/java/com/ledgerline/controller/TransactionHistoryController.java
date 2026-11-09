package com.ledgerline.controller;

import com.ledgerline.dto.TransactionHistoryResponse;
import com.ledgerline.service.TransactionHistoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class TransactionHistoryController {

    private final TransactionHistoryService historyService;

    public TransactionHistoryController(
            TransactionHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable String accountNumber) {

        return historyService.getTransactionHistory(accountNumber);
    }
}