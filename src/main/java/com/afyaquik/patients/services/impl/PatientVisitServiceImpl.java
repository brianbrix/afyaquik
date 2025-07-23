package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.PatientAssignmentDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientAssignment;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.enums.VisitType;
import com.afyaquik.utils.mappers.patients.PatientAssignmentsMapper;
import com.afyaquik.patients.repository.PatientAssignmentsRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.services.PatientVisitService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientVisitServiceImpl implements PatientVisitService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final PatientAssignmentsRepo  patientAssignmentsRepo;
    private final PatientAssignmentsMapper  patientAssignmentsMapper;
    @Override
    public PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        PatientVisit patientVisit = PatientVisit.builder()
                .patient(patient)
                .summaryReasonForVisit(patientVisitDto.getSummaryReasonForVisit())
                .visitDate(LocalDate.now())
                .visitType(VisitType.valueOf(patientVisitDto.getVisitType()))
                .build();
        patientVisit.setVisitStatus(Status.STARTED);
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
            patientVisit.setVisitStatus(Status.valueOf(patientVisitDto.getVisitStatus()));
        }
        else {
            patientVisit.setVisitStatus(Status.STARTED); //todo work on this status
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
    public void updatePatientVisitStatus(Long visitId, String status) {
        PatientVisit  patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        patientVisit.setVisitStatus(Status.valueOf(status));
        if (patientVisit.getVisitStatus().equals(Status.COMPLETED) || patientVisit.getVisitStatus().equals(Status.CANCELLED)) {
            patientVisit.getPatientAssignments()
                    .forEach(assignment -> {
                        assignment.setAssignmentStatus(Status.COMPLETED);
//                        patientAssignmentsRepo.save(assignment);
                    });
        }
        patientVisitRepository.save(patientVisit);
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
               if (detailsType.contains("assignment"))
               {
                   patientVisitDto.setAssignments(patientVisit.getPatientAssignments()!=null?
                           patientVisit.getPatientAssignments().stream().map(plan -> PatientAssignmentDto.builder()
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
    public PatientVisit getPatientVisit(Long visitId) {
        return patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
    }

    @Override
    public ListFetchDto<PatientAssignmentDto> getAssignments(Long visitId, Pageable pageable) {
        PatientVisit  patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        return ListFetchDto.<PatientAssignmentDto>builder()
                .results(
                        patientAssignmentsRepo.findAllByPatientVisitId(patientVisit.getId(), pageable).map(patientAssignmentsMapper::toDto)
                )
                .build();
    }


    @Override
    public ListFetchDto<PatientAssignmentDto> getAssignmentsForOfficer(Long visitId, Long officerId, String whichOfficer, Pageable pageable) {
        PatientVisit patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        if (whichOfficer==null) {
            throw new IllegalArgumentException("Officer type not specified");
        }
        if (whichOfficer.equalsIgnoreCase("attending")) {
            return ListFetchDto.<PatientAssignmentDto>builder()
                    .results(
                            patientAssignmentsRepo.findAllByPatientVisitIdAndAttendingOfficerId(patientVisit.getId(), officerId, pageable).map(patientAssignmentsMapper::toDto)
                    )
                    .build();
        }
        else if (whichOfficer.equalsIgnoreCase("assigned")) {
            return ListFetchDto.<PatientAssignmentDto>builder()
                    .results(
                            patientAssignmentsRepo.findAllByPatientVisitIdAndAssignedOfficerId(patientVisit.getId(), officerId, pageable).map(patientAssignmentsMapper::toDto)
                    )
                    .build();
        }
        else {
            throw new EntityNotFoundException("Invalid officer type");
        }
    }

    @Transactional
    @Override
    public void updateAssignmentStatus(Long assignmentId, String status) {
        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        patientAssignmentsRepo.findByIdWithVisitAndAssignments(assignmentId)
                .ifPresentOrElse(assignment -> {
                    assignment.setAssignmentStatus(newStatus);
                    patientAssignmentsRepo.save(assignment);

                    if (newStatus == Status.COMPLETED) {
                        boolean allCompleted = assignment.getPatientVisit()
                                .getPatientAssignments()
                                .stream()
                                .allMatch(a -> a.getAssignmentStatus() == Status.COMPLETED);

                        if (allCompleted) {
                            assignment.getPatientVisit().setVisitStatus(Status.COMPLETED);
                            patientVisitRepository.save(assignment.getPatientVisit());
                        }
                    }
                }, () -> {
                    throw new EntityNotFoundException("Patient assignment not found");
                });
    }

    @Override
    public PatientAssignmentDto getAssignments(Long planId) {
        return patientAssignmentsMapper.toDto(patientAssignmentsRepo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Patient Assignment not found")));
    }
}
