
package com.ledgerline.service;

import com.ledgerline.dto.DepositRequest;
import com.ledgerline.entity.Account;
import com.ledgerline.entity.Deposit;
import com.ledgerline.entity.LedgerEntry;
import com.ledgerline.repository.AccountRepository;
import com.ledgerline.repository.DepositRepository;
import com.ledgerline.repository.LedgerEntryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final DepositRepository depositRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public DepositService(
            AccountRepository accountRepository,
            DepositRepository depositRepository,
            LedgerEntryRepository ledgerEntryRepository) {

        this.accountRepository = accountRepository;
        this.depositRepository = depositRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public Deposit depositMoney(DepositRequest request) {

        String accountNumber = request.getAccountNumber();
        BigDecimal amount = request.getAmount();
        String key = request.getIdempotencyKey();

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Account number is required"
            );
        }

        if (amount == null || amount.signum() <= 0
                || amount.scale() > 2) {
            throw new IllegalArgumentException(
                "Amount must be positive and have at most two decimal places"
            );
        }

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                "Idempotency key is required"
            );
        }

        // Lock the account during the deposit.
        Account account = accountRepository
            .findByAccountNumberForUpdate(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException(
                "Account not found"
            ));

        // Check whether this request was already processed.
        var existingDeposit = depositRepository
            .findByIdempotencyKey(key);

        if (existingDeposit.isPresent()) {
            Deposit existing = existingDeposit.get();

            boolean sameRequest =
                existing.getAccount().getId().equals(account.getId())
                && existing.getAmount().compareTo(amount) == 0;

            if (!sameRequest) {
                throw new IllegalArgumentException(
                    "Idempotency key already used for a different deposit"
                );
            }

            return existing;
        }

        // Update account balance.
        account.setBalance(
            account.getBalance().add(amount)
        );

        accountRepository.save(account);

        // Save deposit record.
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setAmount(amount);
        deposit.setCurrency(account.getCurrency());
        deposit.setIdempotencyKey(key);
        deposit.setStatus(Deposit.DepositStatus.SUCCESS);

        deposit = depositRepository.save(deposit);

        // Record the credit in the ledger.
        LedgerEntry credit = new LedgerEntry();
        credit.setDeposit(deposit);
        credit.setAccount(account);
        credit.setEntryType(LedgerEntry.EntryType.CREDIT);
        credit.setAmount(amount);

        ledgerEntryRepository.save(credit);

        return deposit;
    }
}
