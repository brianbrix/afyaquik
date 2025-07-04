package com.afyaquik.users.service;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.entity.Role;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApiPermissionService {
    List<ApiPermissionDto> getEnabledPermissions();
    Optional<ApiPermission> getPermissionByUrlPattern(String urlPattern);
    List<ApiPermission> getPermissionsByRole(Role role);
    ApiPermissionDto savePermission(ApiPermissionDto permissionDto);
    void deletePermission(Long id);
    List<String> getUrlPatternsForRole(String roleName);
    ApiPermissionDto getPermissionById(Long id);
    ApiPermissionDto updatePermission(ApiPermissionDto permissionDto, Long id);
}
