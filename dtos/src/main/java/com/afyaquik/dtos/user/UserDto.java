package com.afyaquik.dtos.user;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "First name is required")
    private String firstName;
    private String secondName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private String fullName;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    private Boolean enabled=true;
    private Set<String> roles= new HashSet<>();
    private boolean available;
    private Set<String> stations = new HashSet<>();
    public String getFullName() {
        return (firstName + " " + secondName + " " + lastName).replaceAll("null", "");
    }
}



