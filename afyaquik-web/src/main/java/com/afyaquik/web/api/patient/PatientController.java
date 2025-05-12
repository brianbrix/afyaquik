package com.afyaquik.web.api.patient;

import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.patients.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.createPatient(patientDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchPatient(@RequestBody PatientDto patientDto) {
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
