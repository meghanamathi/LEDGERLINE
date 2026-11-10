
package com.ledgerline.controller;

import com.ledgerline.dto.LedgerVerificationResponse;
import com.ledgerline.entity.Transfer;
import com.ledgerline.repository.TransferRepository;
import com.ledgerline.service.LedgerVerificationService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ledger")
public class LedgerVerificationController {

    private final LedgerVerificationService verificationService;
    private final TransferRepository transferRepository;

    public LedgerVerificationController(
            LedgerVerificationService verificationService,
            TransferRepository transferRepository) {
        this.verificationService = verificationService;
        this.transferRepository = transferRepository;
    }

    @GetMapping("/verify/{transferId}")
    public LedgerVerificationResponse verifyTransfer(
            @PathVariable Long transferId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transfer not found"
                ));

        boolean isSenderOwner =
                transfer.getSender().getOwner() != null
                && transfer.getSender().getOwner()
                        .getEmail().equals(email);

        boolean isReceiverOwner =
                transfer.getReceiver().getOwner() != null
                && transfer.getReceiver().getOwner()
                        .getEmail().equals(email);

        if (!isSenderOwner && !isReceiverOwner) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transfer not found"
            );
        }

        return verificationService.verifyTransfer(transferId);
    }
}
