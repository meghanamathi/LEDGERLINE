package com.ledgerline.service;

import com.ledgerline.dto.TransactionHistoryResponse;
import com.ledgerline.entity.Account;
import com.ledgerline.entity.Deposit;
import com.ledgerline.entity.LedgerEntry;
import com.ledgerline.entity.Transfer;
import com.ledgerline.repository.AccountRepository;
import com.ledgerline.repository.LedgerEntryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class TransactionHistoryService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TransactionHistoryService(
            AccountRepository accountRepository,
            LedgerEntryRepository ledgerEntryRepository) {

        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(
            String accountNumber) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));

        List<LedgerEntry> entries =
                ledgerEntryRepository.findByAccountId(account.getId());

        return entries.stream()
                .sorted(Comparator.comparing(
                        LedgerEntry::getCreatedAt,
                        Comparator.reverseOrder()))
                .map(this::convertToResponse)
                .toList();
    }

    private TransactionHistoryResponse convertToResponse(
            LedgerEntry entry) {

        if (entry.getDeposit() != null) {

            Deposit deposit = entry.getDeposit();

            return new TransactionHistoryResponse(
                    deposit.getId(),
                    "DEPOSIT",
                    "CREDIT",
                    entry.getAmount(),
                    deposit.getCurrency(),
                    deposit.getStatus().name(),
                    entry.getCreatedAt(),
                    "Money deposited into account"
            );
        }

        Transfer transfer = entry.getTransfer();

        String direction = entry.getEntryType().name();

        String description = direction.equals("DEBIT")
                ? "Transfer to " + transfer.getReceiver().getAccountNumber()
                : "Transfer from " + transfer.getSender().getAccountNumber();

        return new TransactionHistoryResponse(
                transfer.getId(),
                "TRANSFER",
                direction,
                entry.getAmount(),
                transfer.getCurrency(),
                transfer.getStatus().name(),
                entry.getCreatedAt(),
                description
        );
    }
}