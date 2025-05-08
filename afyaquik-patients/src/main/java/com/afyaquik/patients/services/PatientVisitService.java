package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.PatientVisitDto;

public interface PatientVisitService {
    PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId);
    PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto);
}
