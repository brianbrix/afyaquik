package com.afyaquik.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInfo {
    private String phoneNumber;
    private String phoneNumber2;
    private String email;
    private String address;
}
