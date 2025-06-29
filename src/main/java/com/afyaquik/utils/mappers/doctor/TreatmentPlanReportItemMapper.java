package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanReportItemDto;
import com.afyaquik.doctor.entity.TreatmentPlanReportItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TreatmentPlanReportItemMapper extends EntityMapper<TreatmentPlanReportItem, TreatmentPlanReportItemDto> {
    @Override
    default TreatmentPlanReportItemDto toDto(TreatmentPlanReportItem entity) {
        return TreatmentPlanReportItemDto.builder()
                .id(entity.getId())
                .treatmentPlanId(entity.getTreatmentPlan().getId())
                .treatmentPlanItemId(entity.getTreatmentPlanItem().getId())
                .treatmentPlanItemName(entity.getTreatmentPlanItem().getName())
                .reportDetails(entity.getReportDetails())
                .build();
    }
}
