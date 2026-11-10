
package com.ledgerline.dto;

public class LoginResponse {

    private String status;
    private String message;
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;

    public LoginResponse(
            String status,
            String message,
            String token,
            Long userId,
            String fullName,
            String email,
            String role) {
        this.status = status;
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
