package com.afyaquik.web.api.patient;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.PatientVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient/visits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEPTIONIST') or hasRole('NURSE') or hasRole('DOCTOR')")
public class PatientVisitController {
    private final PatientService patientService;
    private final PatientVisitService  patientVisitService;

    @PutMapping("/update")
    public ResponseEntity<PatientVisitDto> updateVisit(@RequestBody PatientVisitDto  patientVisitDto) {
        return ResponseEntity.ok(patientVisitService.updatePatientVisit(patientVisitDto));
    }
    @PostMapping("/plan/create")
    public ResponseEntity<?> createAttendingPlan(@RequestBody PatientAttendingPlanDto planDto) {
        return ResponseEntity.ok(patientService.createPatientAttendingPlan(planDto));
    }
    @PutMapping("/plan/update")
    public ResponseEntity<?> updateAttendingPlan(@RequestBody PatientAttendingPlanDto planDto) {
        return ResponseEntity.ok(patientService.updatePatientAttendingPlan(planDto));
    }
    @GetMapping("/{patientId}")
    public ResponseEntity<?> getVisits(@PathVariable Long  patientId) {
        return ResponseEntity.ok(patientService.getPatientVisits(patientId));
    }

}
