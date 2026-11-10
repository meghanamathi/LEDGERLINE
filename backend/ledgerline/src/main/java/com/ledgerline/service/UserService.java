
package com.ledgerline.service;

import com.ledgerline.dto.UserRegistrationRequest;
import com.ledgerline.entity.User;
import com.ledgerline.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Email is already registered"
            );
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(
                "Phone number is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);

        // Encrypt password before storing it.
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        // New users always receive the USER role.
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        // Record successful registration.
        auditLogService.log(
            savedUser.getEmail(),
            "USER_REGISTRATION",
            "New user registered",
            "SUCCESS"
        );

        return savedUser;
    }

    public User findByEmail(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found")
                );
    }
}
