package com.afyaquik.utils.mappers.doctor;


import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.entity.ObservationReport;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = ObservationReportItemMapper.class)
public abstract class ObservationReportMapper implements EntityMapper<ObservationReport, ObservationReportDto> {

    @Autowired
    protected ObservationReportItemMapper observationReportItemMapper;

    @Override
    public ObservationReportDto toDto(ObservationReport observationReport) {
        ObservationReportDto observationReportDto = ObservationReportDto.builder()
                .id(observationReport.getId())
                .patientVisitId(observationReport.getPatientVisit().getId())
                .patientName(observationReport.getPatientVisit().getPatient().getFirstName())
                .doctorName(observationReport.getDoctor().getUsername())
                .doctorId(observationReport.getDoctor().getId())
                .createdAt(observationReport.getCreatedAt().toString())
                .updatedAt(observationReport.getUpdatedAt().toString())
                .observationReportItems(
                        observationReport.getObservationReportItems().stream()
                                .map(observationReportItemMapper::toDto)
                                .toList()
                )
                .build();
        if (observationReport.getObservationReportItems()!=null && !observationReport.getObservationReportItems().isEmpty()){
            observationReportDto.setStation(observationReport.getObservationReportItems().get(0).getItem().getStation().getName());
        }
        return observationReportDto;
    }
}
