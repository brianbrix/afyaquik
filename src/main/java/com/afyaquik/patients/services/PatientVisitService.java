package com.afyaquik.patients.services;

import com.afyaquik.patients.dto.PatientAssignmentsDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface PatientVisitService {
    PatientVisitDto createPatientVisit(PatientVisitDto patientVisitDto, Long patientId);
    PatientVisitDto updatePatientVisit(PatientVisitDto patientVisitDto);
    PatientVisitDto getPatientVisitDetails(Long visitId, Set<String> detailsType);
    PatientVisit getPatientVisit(Long visitId);
    ListFetchDto<PatientAssignmentsDto> getAssignmentss(Long visitId, Pageable pageable);
    ListFetchDto<PatientAssignmentsDto> getAssignmentsForOfficer(Long visitId, Long officerId, String whichOfficer, Pageable pageable);

 PatientAssignmentsDto getAssignments(Long planId);
}
