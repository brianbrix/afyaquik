package com.afyaquik.users.mappers;


import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.entity.Role;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<Role, RoleResponse> {
    @Override
    RoleResponse toDto(Role entity);
}

