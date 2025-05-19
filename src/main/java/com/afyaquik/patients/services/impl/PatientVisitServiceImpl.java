package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import com.afyaquik.patients.mappers.PatientAttendingPlanMapper;
import com.afyaquik.patients.repository.PatientAttendingPlanRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.services.PatientVisitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientVisitServiceImpl implements PatientVisitService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final PatientAttendingPlanRepo  patientAttendingPlanRepo;
    private final PatientAttendingPlanMapper  patientAttendingPlanMapper;
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


    @Override
    public PatientVisitDto getPatientVisitDetails(Long visitId, Set<String> detailsType) {
        PatientVisit patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        PatientVisitDto patientVisitDto =PatientVisitDto.builder()
                .id(patientVisit.getId())
                .patientId(patientVisit.getPatient().getId())
                .patientName(patientVisit.getPatient().getPatientName())
                .summaryReasonForVisit(patientVisit.getSummaryReasonForVisit())
                .visitDate(patientVisit.getVisitDate())
                .visitType(patientVisit.getVisitType().name())
                .nextVisitDate(patientVisit.getNextVisitDate())
                .visitStatus(patientVisit.getVisitStatus()!=null?patientVisit.getVisitStatus().name():null)
                .build();
                detailsType = detailsType==null?new HashSet<>():detailsType;
               if (detailsType.contains("attendingPlan"))
               {
                   patientVisitDto.setAttendingPlan(patientVisit.getPatientAttendingPlan()!=null?
                           patientVisit.getPatientAttendingPlan().stream().map(plan -> PatientAttendingPlanDto.builder()
                                   .id(plan.getId())
                                   .patientName(plan.getPatientVisit().getPatient().getPatientName())
                                   .nextStation(plan.getNextStation().getName())
                                   .assignedOfficer(plan.getAssignedOfficer().getUsername())
                                   .patientVisitId(plan.getPatientVisit().getId())
                                   .attendingOfficerUserName(plan.getAttendingOfficer().getUsername())
                                   .build()).toList():null);
               }
               return patientVisitDto;
    }

    @Override
    public ListFetchDto<PatientAttendingPlanDto> getVisitAttendingPlan(Long visitId, Pageable pageable) {
        PatientVisit  patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        return ListFetchDto.<PatientAttendingPlanDto>builder()
                .results(
                        patientAttendingPlanRepo.findAllByPatientVisitId(patientVisit.getId(), pageable).map(patientAttendingPlanMapper::toDto)
                )
                .build();
    }
}
