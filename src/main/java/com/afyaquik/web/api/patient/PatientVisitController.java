package com.afyaquik.web.api.patient;

import com.afyaquik.patients.dto.PatientAssignmentDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.PatientVisitService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/patient/visits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEPTIONIST') or hasRole('NURSE') or hasRole('DOCTOR')")
public class PatientVisitController {
    private final PatientService patientService;
    private final PatientVisitService  patientVisitService;

    @GetMapping("/{visitId}")
    public ResponseEntity<PatientVisitDto> getPatientVisitDetails(@PathVariable Long visitId, @RequestParam(required = false) Set<String> detailsType, Pageable pageable) {
        return ResponseEntity.ok(patientVisitService.getPatientVisitDetails(visitId, detailsType));
    }

    @GetMapping("/{visitId}/assignments")
    public ResponseEntity<ListFetchDto<PatientAssignmentDto>> getPatientAssignments(@PathVariable Long visitId , Pageable pageable) {
        return ResponseEntity.ok(patientVisitService.getAssignments(visitId, pageable));
    }


    @GetMapping("/assignments/{planId}")
    public ResponseEntity<PatientAssignmentDto> getPatientAssignments(@PathVariable Long planId) {
        return ResponseEntity.ok(patientVisitService.getAssignments(planId));
    }
    @GetMapping("/{visitId}/assignments/{officerId}")
    public ResponseEntity<ListFetchDto<PatientAssignmentDto>> getPatientAssignmentsForOfficer(@PathVariable Long visitId , @PathVariable Long officerId, @RequestParam(required = false)String whichOfficer, Pageable  pageable) {
        return ResponseEntity.ok(patientVisitService.getAssignmentsForOfficer(visitId,officerId, whichOfficer, pageable));
    }

    @PutMapping("/update")
    public ResponseEntity<PatientVisitDto> updateVisit(@RequestBody PatientVisitDto  patientVisitDto) {
        return ResponseEntity.ok(patientVisitService.updatePatientVisit(patientVisitDto));
    }
    @PostMapping("/assignments/create")
    public ResponseEntity<PatientAssignmentDto> createAssignments(@RequestBody PatientAssignmentDto planDto) {
        return ResponseEntity.ok(patientService.createPatientAssignments(planDto));
    }
    @PutMapping("/assignments/update")
    public ResponseEntity<PatientAssignmentDto> updateAssignments(@RequestBody PatientAssignmentDto planDto) {
        return ResponseEntity.ok(patientService.updatePatientAssignments(planDto));
    }


}
