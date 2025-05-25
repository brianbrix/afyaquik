package com.afyaquik.doctor.service;

import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.dto.ObservationReportItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.doctor.entity.ObservationReport;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface ObservationService {
    ObservationItemDto addObservationItem(ObservationItemDto observationItemDto);
    ObservationItemDto updateObservationItem(Long id,ObservationItemDto observationItemDto);
    ObservationItemDto getObservationItem(Long id);
    ListFetchDto<ObservationItemDto> getObservationItems(Pageable pageable);
    void deleteObservationItem(Long id);

    ObservationReportDto addObservationReport(ObservationReportDto observationReportDto);
    ObservationReportDto updateObservationReport(Long id, ObservationReportDto observationReportDto);
    ObservationReportDto getObservationReport(Long id);
    void deleteObservationReport(Long id);

    ObservationReportItemDto getObservationReportItem(Long id);

    ListFetchDto<ObservationReportDto> getPatientReports(Long patientId, Pageable  pageable);
}
