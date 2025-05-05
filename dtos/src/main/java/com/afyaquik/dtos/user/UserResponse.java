package com.afyaquik.dtos.user;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String secondName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
