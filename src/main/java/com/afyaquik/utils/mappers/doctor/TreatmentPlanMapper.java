package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.entity.TreatmentPlan;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TreatmentPlanMapper extends EntityMapper<TreatmentPlan, TreatmentPlanDto> {

    @Override
    default TreatmentPlanDto toDto(TreatmentPlan entity) {
        return TreatmentPlanDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .treatmentPlanItemName(entity.getTreatmentPlanItem().getName())
                .treatmentPlanItemId(entity.getTreatmentPlanItem().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .patientName(entity.getPatientVisit().getPatient().getFirstName() + " " + entity.getPatientVisit().getPatient().getLastName())
                .doctorName(entity.getDoctor().getFirstName() + " " + entity.getDoctor().getLastName())
                .patientVisitId(entity.getPatientVisit().getId())
                .doctorId(entity.getDoctor().getId())
                .build();
    }
}
