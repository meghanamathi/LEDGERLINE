
package com.ledgerline.service;

import com.ledgerline.dto.UserResponse;
import com.ledgerline.dto.AdminUserDetailsResponse;
import com.ledgerline.entity.User;
import com.ledgerline.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public AdminUserService(
            UserRepository userRepository,
            AuditLogService auditLogService) {

        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public AdminUserDetailsResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return new AdminUserDetailsResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public String updateUserStatus(Long userId, boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setActive(active);
        userRepository.save(user);

        // Get the authenticated administrator.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String adminEmail = authentication != null
                ? authentication.getName()
                : "UNKNOWN";

        // Record the admin action.
        auditLogService.log(
                adminEmail,
                active ? "USER_UNBLOCKED" : "USER_BLOCKED",
                "Admin " + adminEmail
                        + (active ? " unblocked user " : " blocked user ")
                        + user.getEmail(),
                "SUCCESS"
        );

        return active
                ? "User activated successfully"
                : "User blocked successfully";
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
