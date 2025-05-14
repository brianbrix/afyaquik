package com.afyaquik.web.api.patient;

import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.PatientVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientVisitService patientVisitService;

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.createPatient(patientDto));
    }
    @PostMapping("/{patientId}/visits/create")
    public ResponseEntity<PatientVisitDto> createVisit(@RequestBody PatientVisitDto  patientVisitDto, @PathVariable Long patientId) {
        return ResponseEntity.ok(patientVisitService.createPatientVisit(patientVisitDto, patientId));
    }

    @GetMapping("/{patientId}/visits")
    public ResponseEntity<List<PatientVisitDto>> getVisits(@PathVariable Long  patientId) {
        return ResponseEntity.ok(patientService.getPatientVisits(patientId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }
    @PostMapping("/search")
    public ResponseEntity<List<PatientDto>> searchPatient(@RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.filterPatients(patientDto));
    }
    @PutMapping("/{id}/update")
    public ResponseEntity<PatientDto> updatePatient(@RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.updatePatient(patientDto));
    }
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> deletePatient(@RequestBody PatientDto patientDto) {
        patientService.deletePatient(patientDto.getId());
        return ResponseEntity.ok(null);
    }

}
