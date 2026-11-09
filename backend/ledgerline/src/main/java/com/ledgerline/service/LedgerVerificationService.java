package com.ledgerline.service;

import com.ledgerline.dto.LedgerVerificationResponse;
import com.ledgerline.entity.LedgerEntry;
import com.ledgerline.entity.Transfer;
import com.ledgerline.repository.LedgerEntryRepository;
import com.ledgerline.repository.TransferRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LedgerVerificationService {

    private final TransferRepository transferRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerVerificationService(
            TransferRepository transferRepository,
            LedgerEntryRepository ledgerEntryRepository) {

        this.transferRepository = transferRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public LedgerVerificationResponse verifyTransfer(Long transferId) {

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Transfer not found"));

        List<LedgerEntry> entries =
                ledgerEntryRepository.findByTransferId(transferId);

        BigDecimal debitTotal = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;

        int debitCount = 0;
        int creditCount = 0;

        for (LedgerEntry entry : entries) {

            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                debitTotal = debitTotal.add(entry.getAmount());
                debitCount++;
            }

            if (entry.getEntryType() == LedgerEntry.EntryType.CREDIT) {
                creditTotal = creditTotal.add(entry.getAmount());
                creditCount++;
            }
        }

        boolean valid =
                transfer.getStatus() == Transfer.TransferStatus.SUCCESS
                && entries.size() == 2
                && debitCount == 1
                && creditCount == 1
                && debitTotal.compareTo(creditTotal) == 0
                && debitTotal.compareTo(transfer.getAmount()) == 0;

        String message = valid
                ? "Transfer ledger is balanced and verified"
                : "Ledger verification failed";

        return new LedgerVerificationResponse(
                transferId,
                valid,
                entries.size(),
                debitTotal,
                creditTotal,
                message
        );
    }
}