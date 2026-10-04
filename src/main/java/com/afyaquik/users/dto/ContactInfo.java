package com.afyaquik.users.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;

@Data
@Builder
public class ContactInfo {
    private String phoneNumber;
    private String phoneNumber2;
    @Email(message = "Enter a valid email address")
    private String email;
    private String address;
}
