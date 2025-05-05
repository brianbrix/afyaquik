package com.afyaquik.dtos.user;

import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String secondname;
    private String password;
    private Set<String> roles;
}



