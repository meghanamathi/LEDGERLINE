package com.ledgerline.controller;

import com.ledgerline.dto.LedgerVerificationResponse;
import com.ledgerline.service.LedgerVerificationService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ledger")
public class LedgerVerificationController {

    private final LedgerVerificationService verificationService;

    public LedgerVerificationController(
            LedgerVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify/{transferId}")
    public LedgerVerificationResponse verifyTransfer(
            @PathVariable Long transferId) {

        return verificationService.verifyTransfer(transferId);
    }
}