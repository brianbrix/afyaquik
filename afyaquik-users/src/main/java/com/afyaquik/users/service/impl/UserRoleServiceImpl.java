package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.RoleRequest;
import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.service.UserRoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final RolesRepository rolesRepository;
    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        rolesRepository.findByName(roleRequest.getName()).ifPresent(role -> {
            throw new IllegalArgumentException("Role already exists");
        });
        Role role = rolesRepository.save(Role.builder().name(roleRequest.getName()).build());
        return RoleResponse.builder().id(role.getId()).name(role.getName()).build();
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
       Role  role = rolesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));
       role.setName(roleRequest.getName());
       rolesRepository.save(role);
       return RoleResponse.builder().id(role.getId()).name(role.getName()).build();
    }

    @Override
    public void deleteRole(Long id) {
    rolesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));
    rolesRepository.deleteById(id);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return rolesRepository.findAll().stream().map(role -> RoleResponse.builder().id(role.getId()).name(role.getName()).build()).toList();
    }
}
