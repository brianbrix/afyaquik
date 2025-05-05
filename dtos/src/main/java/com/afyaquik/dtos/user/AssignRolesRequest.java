package com.afyaquik.dtos.user;

import lombok.Data;

import java.util.Set;

@Data
public class AssignRolesRequest {
    private Set<String> roles;
}
