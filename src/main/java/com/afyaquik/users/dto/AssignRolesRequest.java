package com.afyaquik.users.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AssignRolesRequest {
    private Set<String> roles;
}
