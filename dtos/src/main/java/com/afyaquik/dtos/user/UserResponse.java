package com.afyaquik.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String secondName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Boolean available;
    private Set<String> roles;
    private Set<String> stations;
}
