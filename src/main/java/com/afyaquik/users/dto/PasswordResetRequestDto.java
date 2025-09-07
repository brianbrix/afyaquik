package com.afyaquik.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetRequestDto {
    private Long userId;
    private String username;
    private String password;
    private String confirmPassword;
}
