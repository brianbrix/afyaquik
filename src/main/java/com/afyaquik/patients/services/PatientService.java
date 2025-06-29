package com.afyaquik.patients.services;

import com.afyaquik.patients.dto.PatientAssignmentsDto;
import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientDto createPatient(PatientDto patientDto);
    PatientDto getPatient(Long id);
    PatientDto updatePatient(PatientDto patientDto);
    void deletePatient(Long id);
    List<PatientDto> filterPatients(PatientDto dto);
    ListFetchDto<PatientVisitDto> getPatientVisits(Pageable pageable, Long patientId);
    PatientAssignmentsDto createPatientAssignments(PatientAssignmentsDto patientAssignmentsDto);
    PatientAssignmentsDto updatePatientAssignments(PatientAssignmentsDto patientAssignmentsDto);


}
