
package com.ledgerline.service;

import com.ledgerline.dto.TransferRequest;
import com.ledgerline.entity.Account;
import com.ledgerline.entity.LedgerEntry;
import com.ledgerline.entity.Transfer;
import com.ledgerline.repository.AccountRepository;
import com.ledgerline.repository.LedgerEntryRepository;
import com.ledgerline.repository.TransferRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AuditLogService auditLogService;

    public TransferService(
            AccountRepository accountRepository,
            TransferRepository transferRepository,
            LedgerEntryRepository ledgerEntryRepository,
            AuditLogService auditLogService) {

        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Transfer transferMoney(TransferRequest request) {

        String senderNumber = request.getSenderAccountNumber();
        String receiverNumber = request.getReceiverAccountNumber();
        BigDecimal amount = request.getAmount();
        String key = request.getIdempotencyKey();

        // Validate sender and receiver.
        if (senderNumber == null || senderNumber.isBlank()
                || receiverNumber == null || receiverNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Sender and receiver account numbers are required"
            );
        }

        if (senderNumber.equals(receiverNumber)) {
            throw new IllegalArgumentException(
                "Sender and receiver cannot be the same"
            );
        }

        // Validate amount.
        if (amount == null || amount.signum() <= 0
                || amount.scale() > 2) {
            throw new IllegalArgumentException(
                "Amount must be positive and have at most two decimal places"
            );
        }

        // Validate idempotency key.
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                "Idempotency key is required"
            );
        }

        // Lock accounts in a consistent order to reduce deadlocks.
        Account sender;
        Account receiver;

        if (senderNumber.compareTo(receiverNumber) < 0) {

            sender = accountRepository
                .findByAccountNumberForUpdate(senderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sender account not found"
                ));

            receiver = accountRepository
                .findByAccountNumberForUpdate(receiverNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Receiver account not found"
                ));

        } else {

            receiver = accountRepository
                .findByAccountNumberForUpdate(receiverNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Receiver account not found"
                ));

            sender = accountRepository
                .findByAccountNumberForUpdate(senderNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sender account not found"
                ));
        }

        // Verify authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                "Authentication is required"
            );
        }

        String loggedInEmail = authentication.getName();

        // Verify sender account ownership.
        if (sender.getOwner() == null ||
                !sender.getOwner().getEmail().equals(loggedInEmail)) {
            throw new IllegalArgumentException(
                "You are not authorized to transfer from this account"
            );
        }

        // Check for an earlier request with the same idempotency key.
        var existingTransfer = transferRepository
            .findByIdempotencyKey(key);

        if (existingTransfer.isPresent()) {

            Transfer existing = existingTransfer.get();

            boolean sameRequest =
                existing.getSender().getId().equals(sender.getId())
                && existing.getReceiver().getId().equals(receiver.getId())
                && existing.getAmount().compareTo(amount) == 0;

            if (!sameRequest) {
                throw new IllegalArgumentException(
                    "Idempotency key already used for a different request"
                );
            }

            return existing;
        }

        // Validate currency.
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new IllegalArgumentException(
                "Sender and receiver currencies must match"
            );
        }

        // Validate sufficient balance.
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalStateException(
                "Insufficient account balance"
            );
        }

        // Debit sender and credit receiver.
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Create transfer record.
        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setAmount(amount);
        transfer.setCurrency(sender.getCurrency());
        transfer.setIdempotencyKey(key);
        transfer.setStatus(Transfer.TransferStatus.SUCCESS);

        transfer = transferRepository.save(transfer);

        // Create debit ledger entry.
        LedgerEntry debit = new LedgerEntry();
        debit.setTransfer(transfer);
        debit.setAccount(sender);
        debit.setEntryType(LedgerEntry.EntryType.DEBIT);
        debit.setAmount(amount);

        // Create credit ledger entry.
        LedgerEntry credit = new LedgerEntry();
        credit.setTransfer(transfer);
        credit.setAccount(receiver);
        credit.setEntryType(LedgerEntry.EntryType.CREDIT);
        credit.setAmount(amount);

        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);

        // Record successful transfer in audit log.
        auditLogService.log(
            loggedInEmail,
            "MONEY_TRANSFER",
            "Transferred " + amount + " " + sender.getCurrency()
                    + " from account " + senderNumber
                    + " to account " + receiverNumber,
            "SUCCESS"
        );

        return transfer;
    }
}
