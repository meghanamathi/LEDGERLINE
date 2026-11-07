package com.ledgerline.service;

import com.ledgerline.entity.Account;
import com.ledgerline.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(String holderName) {

        Account account = new Account();

        account.setAccountNumber(
            "LL" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase()
        );

        account.setHolderName(holderName);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}