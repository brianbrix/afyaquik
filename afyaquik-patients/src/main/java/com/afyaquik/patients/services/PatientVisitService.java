package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.PatientVisitDto;

import java.util.Set;

public interface PatientVisitService {
    PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId);
    PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto);
    PatientVisitDto getPatientVisitDetails(Long visitId, Set<String> detailsType);
}
