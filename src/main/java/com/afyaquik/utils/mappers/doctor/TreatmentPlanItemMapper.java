package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanItemDto;
import com.afyaquik.doctor.entity.TreatmentPlanItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TreatmentPlanItemMapper extends EntityMapper<TreatmentPlanItem, TreatmentPlanItemDto> {
    @Override
    TreatmentPlanItemDto toDto(TreatmentPlanItem entity);
}
