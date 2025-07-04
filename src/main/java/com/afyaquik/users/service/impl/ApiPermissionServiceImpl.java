package com.afyaquik.users.service.impl;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.repository.ApiPermissionRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.service.ApiPermissionService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.users.ApiPermissionMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements ApiPermissionService {
    private final ApiPermissionRepository apiPermissionRepository;
    private final RolesRepository rolesRepository;
    private final ApiPermissionMapper apiPermissionMapper;


    @Override
    public List<ApiPermissionDto> getEnabledPermissions() {
        return apiPermissionRepository.findByEnabledTrue().stream().map(apiPermissionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ApiPermission> getPermissionByUrlPattern(String urlPattern) {
        return apiPermissionRepository.findByUrlPattern(urlPattern);
    }

    @Override
    public List<ApiPermission> getPermissionsByRole(Role role) {
        return apiPermissionRepository.findByAllowedRolesContaining(role);
    }

    @Override
    public ApiPermissionDto savePermission(ApiPermissionDto permissionDto) {
        ApiPermission permission = ApiPermission.builder()
                .urlPattern(permissionDto.getUrlPattern())
                .enabled(permissionDto.isEnabled())
                .allowedRoles(permissionDto.getRoleNames().stream()
                        .map(role -> rolesRepository.findByName(role).orElse(null))
                        .collect(Collectors.toSet()))
                .description(permissionDto.getDescription())
                .build();
        return apiPermissionMapper.toDto(apiPermissionRepository.save(permission));
    }

    @Override
    public void deletePermission(Long id) {
        apiPermissionRepository.deleteById(id);
    }

    @Override
    public List<String> getUrlPatternsForRole(String roleName) {
        Optional<Role> roleOpt = rolesRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return List.of();
        }

        Role role = roleOpt.get();
        return apiPermissionRepository.findByAllowedRolesContaining(role)
                .stream()
                .filter(ApiPermission::isEnabled)
                .map(ApiPermission::getUrlPattern)
                .collect(Collectors.toList());
    }

    @Override
    public ApiPermissionDto getPermissionById(Long id) {
        return apiPermissionMapper.toDto(apiPermissionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Permission not found")));
    }

    @Override
    public ApiPermissionDto updatePermission(ApiPermissionDto permissionDto, Long id) {
        ApiPermission apiPermission =  apiPermissionRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Permission not found"));
        apiPermission.setEnabled(permissionDto.isEnabled());
        apiPermission.setUrlPattern(permissionDto.getUrlPattern());
        apiPermission.setDescription(permissionDto.getDescription());
        apiPermission.getAllowedRoles().addAll(permissionDto.getRoleNames().stream()
                .map(role -> rolesRepository.findByName(role).orElse(null))
                .collect(Collectors.toSet()));
        return apiPermissionMapper.toDto(apiPermissionRepository.save(apiPermission));
    }
}
