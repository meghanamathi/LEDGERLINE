
package com.ledgerline.controller;

import com.ledgerline.dto.UserResponse;
import com.ledgerline.dto.AdminUserDetailsResponse;
import com.ledgerline.service.AdminUserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        return Map.of(
            "status", "SUCCESS",
            "message", "Welcome to the LedgerLine Admin Dashboard"
        );
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public AdminUserDetailsResponse getUserById(
            @PathVariable Long userId) {
        return adminUserService.getUserById(userId);
    }

    @PutMapping("/users/{userId}/status")
    public Map<String, String> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {

        String message =
                adminUserService.updateUserStatus(userId, active);

        return Map.of(
                "status", "SUCCESS",
                "message", message
        );
    }
}
