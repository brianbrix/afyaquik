package com.afyaquik.patients.mappers;

import com.afyaquik.core.mappers.EntityMapper;
import com.afyaquik.dtos.patient.TriageItemDto;
import com.afyaquik.patients.entity.TriageItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TriageItemMapper extends EntityMapper<TriageItem, TriageItemDto> {
    @Override
    TriageItemDto toDto(TriageItem entity);
    TriageItem  toEntity(TriageItemDto dto);
}
