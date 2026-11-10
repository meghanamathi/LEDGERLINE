
package com.ledgerline.dto;

import java.time.LocalDateTime;

public record AdminUserDetailsResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String role,
        LocalDateTime createdAt
) {}
