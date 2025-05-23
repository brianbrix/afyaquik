package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.TriageItemDto;
import com.afyaquik.patients.entity.TriageItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TriageItemMapper extends EntityMapper<TriageItem, TriageItemDto> {
    @Override
    TriageItemDto toDto(TriageItem entity);
    TriageItem  toEntity(TriageItemDto dto);
}
