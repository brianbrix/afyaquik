package com.afyaquik.web.api.patient;

import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.PatientVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientVisitService patientVisitService;

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Validated @RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.createPatient(patientDto));
    }
    @PostMapping("/{patientId}/visits/create")
    public ResponseEntity<PatientVisitDto> createVisit(@Validated @RequestBody PatientVisitDto  patientVisitDto, @PathVariable Long patientId) {
        return ResponseEntity.ok(patientVisitService.createPatientVisit(patientVisitDto, patientId));
    }

    @GetMapping("/{patientId}/visits")
    public ResponseEntity<ListFetchDto<PatientVisitDto>> getVisits(@PathVariable Long  patientId, Pageable pageable) {
        return ResponseEntity.ok(patientService.getPatientVisits(pageable,patientId));
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
