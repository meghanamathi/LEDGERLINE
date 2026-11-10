
package com.ledgerline.service;

import com.ledgerline.dto.LoginRequest;
import com.ledgerline.entity.User;
import com.ledgerline.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    public String login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        // Reject blocked users
        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "Your account is blocked. Contact administrator.");
        }

        // Generate JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // Record successful login
        auditLogService.log(
                user.getEmail(),
                "USER_LOGIN",
                "User logged in successfully",
                "SUCCESS"
        );

        return token;
    }
}
