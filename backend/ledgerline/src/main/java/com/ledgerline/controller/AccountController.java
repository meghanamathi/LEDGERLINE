
package com.ledgerline.controller;

import com.ledgerline.dto.AccountResponse;
import com.ledgerline.entity.Account;
import com.ledgerline.service.AccountService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @RequestBody Map<String, String> request) {

        String holderName = request.get("holderName");

        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Holder name is required");
        }

        Account account = accountService.createAccount(holderName);

        return convertToResponse(account);
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {

        return accountService.getAllAccounts()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private AccountResponse convertToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getHolderName(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedAt()
        );
    }
}
