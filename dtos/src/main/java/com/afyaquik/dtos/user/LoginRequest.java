package com.afyaquik.dtos.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
