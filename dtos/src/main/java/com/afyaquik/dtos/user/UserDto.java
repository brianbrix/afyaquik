package com.afyaquik.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String secondName;
    private String password;
    private Boolean enabled=true;
    private Set<String> roles= new HashSet<>();
    private boolean available;
    private Set<String> stations = new HashSet<>();
    public String getFullName() {
        return (firstName + " " + secondName + " " + lastName).replaceAll("null", "");
    }
}



