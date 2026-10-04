package com.afyaquik.users.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class PasswordResetRequestDto {
    private Long userId;
    @NotBlank(message = "Username is required")
    private String username;
    private String password;
    private String confirmPassword;
}
