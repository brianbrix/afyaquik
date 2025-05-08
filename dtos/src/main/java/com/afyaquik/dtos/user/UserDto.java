package com.afyaquik.dtos.user;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String secondname;
    private String password;
    private Boolean enabled;
    private Set<String> roles;
}
