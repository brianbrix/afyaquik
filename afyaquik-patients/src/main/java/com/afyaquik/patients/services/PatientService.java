package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.dtos.search.SearchResponseDto;
import com.afyaquik.patients.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientDto createPatient(PatientDto patientDto);
    PatientDto getPatient(Long id);
    PatientDto updatePatient(PatientDto patientDto);
    void deletePatient(Long id);
    List<PatientDto> filterPatients(PatientDto dto);
    ListFetchDto<PatientVisitDto> getPatientVisits(Pageable pageable, Long patientId);
    PatientAttendingPlanDto createPatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto);
    PatientAttendingPlanDto updatePatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto);


}
