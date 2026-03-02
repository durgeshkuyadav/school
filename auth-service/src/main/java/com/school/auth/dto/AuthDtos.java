package com.school.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

// ── RegisterRequest ──────────────────────────────────────────────
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class RegisterRequest {
    @NotBlank private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String role;
    private Long profileId;
    private Long classId;
    private String subjectIds;
}

// ── RefreshRequest ───────────────────────────────────────────────
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class RefreshRequest {
    @NotBlank private String refreshToken;
}

// ── ChangePasswordRequest ────────────────────────────────────────
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class ChangePasswordRequest {
    @NotBlank private String oldPassword;
    @NotBlank @Size(min = 8) private String newPassword;
}

// ── UserResponse ─────────────────────────────────────────────────
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Long classId;
    private boolean enabled;
    private LocalDateTime createdAt;
}
