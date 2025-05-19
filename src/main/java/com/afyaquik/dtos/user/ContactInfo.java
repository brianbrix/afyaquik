package com.afyaquik.dtos.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInfo {
    private String phone;
    private String phone2;
    private String email;
    private String address;
}
