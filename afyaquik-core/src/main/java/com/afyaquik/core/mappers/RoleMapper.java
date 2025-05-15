package com.afyaquik.core.mappers;


import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.entity.Role;

import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<Role, RoleResponse> {
    @Override
    RoleResponse toDto(Role entity);
}

