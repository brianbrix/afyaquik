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
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class PatientVisitServiceImpl implements PatientVisitService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final PatientAssignmentsRepo  patientAssignmentsRepo;
    private final PatientAssignmentsMapper  patientAssignmentsMapper;
    @Transactional
    @Override
    public PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId) {

        Patient patient = patientRepository.findForUpdate(patientId)
            .filter(existing -> !existing.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        PatientVisit patientVisit = PatientVisit.builder()
                .patient(patient)
                .summaryReasonForVisit(patientVisitDto.getSummaryReasonForVisit())
                .visitDate(LocalDate.now(ZoneId.of("Africa/Nairobi")))
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
                .visitStatus(savedVisit.getVisitStatus().name())
                .build();



    }

    @Override
    @Transactional
    public PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto) {
        PatientVisit  patientVisit = patientVisitRepository.findForUpdate(patientVisitDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        requireOpenVisit(patientVisit);
        if (patientVisitDto.getSummaryReasonForVisit() != null) patientVisit.setSummaryReasonForVisit(patientVisitDto.getSummaryReasonForVisit());
        if (patientVisitDto.getVisitType() != null) patientVisit.setVisitType(VisitType.valueOf(patientVisitDto.getVisitType()));
        if (patientVisitDto.getNextVisitDate() != null && patientVisitDto.getNextVisitDate().isBefore(patientVisit.getVisitDate())) {
            throw new IllegalArgumentException("The next visit date cannot precede this encounter");
        }
        patientVisit.setNextVisitDate(patientVisitDto.getNextVisitDate());
        if (patientVisitDto.getVisitStatus()!=null) {
            changeStatus(patientVisit, parseClinicalStatus(patientVisitDto.getVisitStatus()));
        }
        patientVisitRepository.save(patientVisit);
        return PatientVisitDto.builder()
                .id(patientVisit.getId())
                .patientId(patientVisit.getPatient().getId())
                .patientName(patientVisit.getPatient().getPatientName())
                .summaryReasonForVisit(patientVisit.getSummaryReasonForVisit())
                .visitDate(patientVisit.getVisitDate())
                .visitType(patientVisit.getVisitType().name())
                .nextVisitDate(patientVisit.getNextVisitDate())
                .inpatientActive(patientVisit.isInpatientActive())
                .visitStatus(patientVisit.getVisitStatus().name())
                .build();
    }

    @Override
    @Transactional
    public void updatePatientVisitStatus(Long visitId, String status) {
        PatientVisit  patientVisit = patientVisitRepository.findForUpdate(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        changeStatus(patientVisit, parseClinicalStatus(status));
        patientVisitRepository.save(patientVisit);
    }

    private Status parseClinicalStatus(String status) {
        if (status == null || !Set.of("PENDING", "STARTED", "IN_PROGRESS", "COMPLETED", "CANCELLED").contains(status.toUpperCase(java.util.Locale.ROOT))) {
            throw new IllegalArgumentException("Select a clinical status; payment status belongs to billing");
        }
        return Status.valueOf(status.toUpperCase(java.util.Locale.ROOT));
    }

    private void requireOpenVisit(PatientVisit visit) {
        if (visit.isDeleted() || visit.getPatient().isDeleted()) throw new IllegalArgumentException("Archived encounters cannot be changed");
        if (visit.getVisitStatus() == Status.COMPLETED || visit.getVisitStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Closed encounters cannot be reopened or edited");
        }
    }

    private void changeStatus(PatientVisit visit, Status next) {
        if (visit.getVisitStatus() == next) return;
        requireOpenVisit(visit);
        if (visit.isInpatientActive() && (next == Status.COMPLETED || next == Status.CANCELLED)) {
            throw new IllegalArgumentException("Record inpatient discharge before closing this encounter");
        }
        if (next == Status.STARTED || next == Status.PENDING) {
            throw new IllegalArgumentException("An encounter cannot be moved backwards in its workflow");
        }
        if (next == Status.COMPLETED) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getAuthorities().stream().noneMatch(authority ->
                    Set.of("ROLE_DOCTOR", "ROLE_ADMIN", "ROLE_SUPERADMIN").contains(authority.getAuthority()))) {
                throw new AccessDeniedException("A clinician must complete the encounter");
            }
            if (visit.getPatientAssignments().stream().anyMatch(assignment -> !assignment.isDeleted()
                    && assignment.getAssignmentStatus() != Status.COMPLETED && assignment.getAssignmentStatus() != Status.CANCELLED)) {
                throw new IllegalArgumentException("Complete or cancel outstanding station assignments first");
            }
        }
        if (next == Status.CANCELLED) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getAuthorities().stream().noneMatch(authority ->
                    Set.of("ROLE_RECEPTIONIST", "ROLE_DOCTOR", "ROLE_ADMIN", "ROLE_SUPERADMIN").contains(authority.getAuthority()))) {
                throw new AccessDeniedException("Only reception, a clinician or an administrator can cancel an encounter");
            }
            visit.getPatientAssignments().stream().filter(assignment -> !assignment.isDeleted()
                    && assignment.getAssignmentStatus() != Status.COMPLETED)
                    .forEach(assignment -> assignment.setAssignmentStatus(Status.CANCELLED));
        }
        visit.setVisitStatus(next);
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
                .inpatientActive(patientVisit.isInpatientActive())
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
    @Transactional
    public PatientVisit getPatientVisit(Long visitId) {
        PatientVisit visit = patientVisitRepository.findForUpdate(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        requireOpenVisit(visit);
        return visit;
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
        Status newStatus = parseClinicalStatus(status);
        Long visitId = patientAssignmentsRepo.findVisitId(assignmentId).orElseThrow(() -> new EntityNotFoundException("Patient assignment not found"));
        patientVisitRepository.findForUpdate(visitId).orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));

        patientAssignmentsRepo.findByIdWithVisitAndAssignments(assignmentId)
                .ifPresentOrElse(assignment -> {
                    var authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication == null || (!assignment.getAssignedOfficer().getUsername().equals(authentication.getName())
                            && authentication.getAuthorities().stream().noneMatch(authority -> Set.of("ROLE_ADMIN", "ROLE_SUPERADMIN").contains(authority.getAuthority())))) {
                        throw new AccessDeniedException("Only the assigned officer or administrator can update this station assignment");
                    }
                    if (assignment.getAssignmentStatus() == newStatus) return;
                    requireOpenVisit(assignment.getPatientVisit());
                    if (assignment.isDeleted() || assignment.getAssignmentStatus() == Status.COMPLETED || assignment.getAssignmentStatus() == Status.CANCELLED
                            || newStatus == Status.PENDING || newStatus == Status.STARTED) {
                        throw new IllegalArgumentException("Invalid station assignment transition");
                    }
                    assignment.setAssignmentStatus(newStatus);
                    patientAssignmentsRepo.save(assignment);
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
