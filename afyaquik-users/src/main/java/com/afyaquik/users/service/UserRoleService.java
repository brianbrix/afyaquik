package com.afyaquik.users.service;

import com.afyaquik.dtos.user.RoleRequest;
import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.entity.Role;

import java.util.List;

public interface UserRoleService {
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse updateRole(Long id, RoleRequest roleRequest);
    void deleteRole(Long id);
    List<RoleResponse> getAllRoles();
}
