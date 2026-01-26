package com.afyaquik.users.service.impl;

import com.afyaquik.users.dto.RoleRequest;
import com.afyaquik.users.dto.RoleResponse;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
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
        if (roleRequest.getName() == null || roleRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }
        if (!roleRequest.getName().startsWith(""))
        {
            roleRequest.setName("" + roleRequest.getName());
        }
        rolesRepository.findByName(roleRequest.getName()).ifPresent(role -> {
            throw new IllegalArgumentException("Role already exists");
        });
        Role role = rolesRepository.save(Role.builder().name(roleRequest.getName()).build());
        return RoleResponse.builder().id(role.getId()).name(role.getName()).build();
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        if (!roleRequest.getName().startsWith(""))
        {
            roleRequest.setName("" + roleRequest.getName());
        }
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

    @Override
    public RoleResponse getRoleByName(String name) {
        Role role = rolesRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Role not found"));
        return  RoleResponse.builder().id(role.getId()).name(role.getName()).stations(role.getStations().stream().map(Station::getName).toList()).build();
    }

    @Override
    public RoleResponse getRole(Long id) {
        return rolesRepository.findById(id).map(role -> RoleResponse.builder().id(role.getId()).name(role.getName()).build()).orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
