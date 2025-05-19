package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface PatientVisitService {
    PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId);
    PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto);
    PatientVisitDto getPatientVisitDetails(Long visitId, Set<String> detailsType);
    ListFetchDto<PatientAttendingPlanDto> getVisitAttendingPlan(Long visitId, Pageable pageable);
}
