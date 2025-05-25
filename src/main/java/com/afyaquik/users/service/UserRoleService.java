package com.afyaquik.users.service;

import com.afyaquik.users.dto.RoleRequest;
import com.afyaquik.users.dto.RoleResponse;

import java.util.List;

public interface UserRoleService {
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse updateRole(Long id, RoleRequest roleRequest);
    void deleteRole(Long id);
    List<RoleResponse> getAllRoles();

    RoleResponse getRole(Long id);
}
