package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.services.PatientVisitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PatientVisitServiceImpl implements PatientVisitService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    @Override
    public PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        PatientVisit patientVisit = PatientVisit.builder()
                .patient(patient)
                .summaryReasonForVisit(patientVisitDto.getSummaryReasonForVisit())
                .visitDate(LocalDateTime.now())
                .visitType(VisitType.valueOf(patientVisitDto.getVisitType()))
                .build();

        PatientVisit savedVisit = patientVisitRepository.save(patientVisit);

        return PatientVisitDto.builder()
                .id(savedVisit.getId())
                .patientId(savedVisit.getPatient().getId())
                .patientName(savedVisit.getPatient().getPatientName())
                .summaryReasonForVisit(savedVisit.getSummaryReasonForVisit())
                .visitDate(savedVisit.getVisitDate())
                .visitType(savedVisit.getVisitType().name())
                .build();



    }

    @Override
    public PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto) {
        PatientVisit  patientVisit = patientVisitRepository.findById(patientVisitDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        patientVisit.setSummaryReasonForVisit(patientVisitDto.getSummaryReasonForVisit());
        patientVisit.setVisitType(VisitType.valueOf(patientVisitDto.getVisitType()));
        patientVisit.setNextVisitDate(patientVisitDto.getNextVisitDate());
        if (patientVisitDto.getVisitStatus()!=null) {
            patientVisit.setVisitStatus(VisitStatus.valueOf(patientVisitDto.getVisitStatus()));
        }
        patientVisitRepository.save(patientVisit);
        return PatientVisitDto.builder()
                .id(patientVisit.getId())
                .patientId(patientVisit.getPatient().getId())
                .patientName(patientVisit.getPatient().getPatientName())
                .summaryReasonForVisit(patientVisit.getSummaryReasonForVisit())
                .visitDate(patientVisit.getVisitDate())
                .visitType(patientVisit.getVisitType().name())
                .build();
    }
}
