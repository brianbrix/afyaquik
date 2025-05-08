package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.patient.TriageReportDto;

import java.util.List;

public interface PatientService {
    PatientDto createPatient(PatientDto patientDto);
    PatientDto getPatient(Long id);
    PatientDto updatePatient(PatientDto patientDto);
    void deletePatient(Long id);
    List<PatientDto> filterPatients(PatientDto dto);
    List<PatientVisitDto> getPatientVisits(Long patientId);
    PatientAttendingPlanDto createPatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto);
    PatientAttendingPlanDto updatePatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto);


}
