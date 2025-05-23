package com.afyaquik.utils.mappers.users;

import com.afyaquik.users.entity.ContactInfo;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactInfoMapper extends EntityMapper<ContactInfo, com.afyaquik.users.dto.ContactInfo> {
    @Override
    com.afyaquik.users.dto.ContactInfo toDto(ContactInfo entity);
}
