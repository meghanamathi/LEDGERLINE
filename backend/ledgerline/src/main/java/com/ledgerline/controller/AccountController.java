
package com.ledgerline.controller;

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
    public Account createAccount(
            @RequestBody Map<String, String> request) {

        String holderName = request.get("holderName");

        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Holder name is required");
        }

        return accountService.createAccount(holderName);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }
}
