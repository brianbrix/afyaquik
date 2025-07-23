package com.afyaquik.patients.services;

import com.afyaquik.patients.dto.PatientAssignmentDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface PatientVisitService {
    PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId);
    PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto);
    void updatePatientVisitStatus(Long visitId, String status);
    PatientVisitDto getPatientVisitDetails(Long visitId, Set<String> detailsType);
    PatientVisit getPatientVisit(Long visitId);
    ListFetchDto<PatientAssignmentDto> getAssignments(Long visitId, Pageable pageable);
    ListFetchDto<PatientAssignmentDto> getAssignmentsForOfficer(Long visitId, Long officerId, String whichOfficer, Pageable pageable);
    void updateAssignmentStatus(Long assignmentId, String status);
 PatientAssignmentDto getAssignments(Long planId);
}
