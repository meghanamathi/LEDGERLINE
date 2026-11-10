
package com.ledgerline.controller;

import com.ledgerline.dto.UserRegistrationRequest;
import com.ledgerline.dto.LoginRequest;
import com.ledgerline.dto.LoginResponse;

import com.ledgerline.entity.User;

import com.ledgerline.service.UserService;
import com.ledgerline.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(
            UserService userService,
            AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // USER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = userService.registerUser(request);

        Map<String, Object> response = Map.of(
            "status", "SUCCESS",
            "message", "User registered successfully",
            "userId", user.getId(),
            "fullName", user.getFullName(),
            "email", user.getEmail(),
            "role", user.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        User user = userService.findByEmail(
                request.getEmail().trim().toLowerCase()
        );

        LoginResponse response = new LoginResponse(
            "SUCCESS",
            "Login successful",
            token,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
