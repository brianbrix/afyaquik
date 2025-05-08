package com.afyaquik.dtos.user;

import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String secondName;
    private String password;
    private Boolean enabled=true;
    private Set<String> roles;
}



