package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.entity.TreatmentPlan;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = TreatmentPlanReportItemMapper.class)
public abstract class TreatmentPlanMapper implements EntityMapper<TreatmentPlan, TreatmentPlanDto> {
    @Autowired
    protected TreatmentPlanReportItemMapper treatmentPlanReportItemMapper;
    @Override
    public TreatmentPlanDto toDto(TreatmentPlan entity) {
        return TreatmentPlanDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .patientName(entity.getPatientVisit().getPatient().getFirstName() + " " + entity.getPatientVisit().getPatient().getLastName())
                .doctorName(entity.getDoctor().getFirstName() + " " + entity.getDoctor().getLastName())
                .patientVisitId(entity.getPatientVisit().getId())
                .doctorId(entity.getDoctor().getId())
                .stationId(entity.getStation().getId())
                .station(entity.getStation().getName())
                .treatmentPlanItemName(null)
                .treatmentPlanReportItems(
                        entity.getTreatmentPlanReportItems() != null ?
                        entity.getTreatmentPlanReportItems().stream()
                                .map(treatmentPlanReportItemMapper::toDto)
                                .toList() : null
                )
                .build();
    }
}
