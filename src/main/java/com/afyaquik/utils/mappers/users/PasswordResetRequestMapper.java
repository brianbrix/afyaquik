package com.afyaquik.utils.mappers.users;

import com.afyaquik.users.dto.PasswordResetRequestDto;
import com.afyaquik.users.entity.PasswordResetRequest;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PasswordResetRequestMapper  extends EntityMapper<PasswordResetRequest, PasswordResetRequestDto> {

    @Mapping(target = "user.id", source = "userId")
    PasswordResetRequest toEntity(PasswordResetRequestDto dto);

    @Override
    @Mapping(target = "userId", source = "user.id")
    PasswordResetRequestDto toDto(PasswordResetRequest entity);
}

