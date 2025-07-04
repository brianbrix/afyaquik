package com.afyaquik.utils.mappers.users;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.entity.Role;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;


import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ApiPermissionMapper extends EntityMapper<ApiPermission, ApiPermissionDto> {
    @Override
    default ApiPermissionDto toDto(ApiPermission permission){
        return ApiPermissionDto.builder()
                .id(permission.getId())
                .urlPattern(permission.getUrlPattern())
                .description(permission.getDescription())
                .enabled(permission.isEnabled())
                .roleNames(permission.getAllowedRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

}
