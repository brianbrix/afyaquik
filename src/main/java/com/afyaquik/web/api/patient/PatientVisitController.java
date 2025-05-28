package com.afyaquik.web.api.patient;

import com.afyaquik.patients.dto.PatientAttendingPlanDto;
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

    @GetMapping("/{visitId}/plans")
    public ResponseEntity<ListFetchDto<PatientAttendingPlanDto>> getPatientVisitAttendingPlans(@PathVariable Long visitId , Pageable pageable) {
        return ResponseEntity.ok(patientVisitService.getVisitAttendingPlans(visitId, pageable));
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<PatientAttendingPlanDto> getPatientVisitAttendingPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(patientVisitService.getVisitAttendingPlan(planId));
    }
    @GetMapping("/{visitId}/plans/{officerId}")
    public ResponseEntity<ListFetchDto<PatientAttendingPlanDto>> getPatientVisitAttendingPlanForOfficer(@PathVariable Long visitId , @PathVariable Long officerId, @RequestParam(required = false)String whichOfficer, Pageable  pageable) {
        return ResponseEntity.ok(patientVisitService.getVisitAttendingPlanForOfficer(visitId,officerId, whichOfficer, pageable));
    }

    @PutMapping("/update")
    public ResponseEntity<PatientVisitDto> updateVisit(@RequestBody PatientVisitDto  patientVisitDto) {
        return ResponseEntity.ok(patientVisitService.updatePatientVisit(patientVisitDto));
    }
    @PostMapping("/plans/create")
    public ResponseEntity<PatientAttendingPlanDto> createAttendingPlan(@RequestBody PatientAttendingPlanDto planDto) {
        return ResponseEntity.ok(patientService.createPatientAttendingPlan(planDto));
    }
    @PutMapping("/plans/update")
    public ResponseEntity<PatientAttendingPlanDto> updateAttendingPlan(@RequestBody PatientAttendingPlanDto planDto) {
        return ResponseEntity.ok(patientService.updatePatientAttendingPlan(planDto));
    }


}
