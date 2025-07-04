package com.afyaquik.web.api.pharmacy;

import com.afyaquik.pharmacy.dto.PatientDrugDto;
import com.afyaquik.pharmacy.services.PatientDrugService;
import com.afyaquik.utils.dto.search.ListFetchDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient-drugs")
@RequiredArgsConstructor
public class PatientDrugController {
    private final PatientDrugService patientDrugService;

    @PostMapping
    public ResponseEntity<PatientDrugDto> assignDrugToPatientVisit(@RequestBody PatientDrugDto patientDrugDto) {
        return ResponseEntity.ok(patientDrugService.assignDrugToPatientVisit(patientDrugDto));
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<PatientDrugDto>> assignManyDrugsToPatientVisit(@RequestBody List<PatientDrugDto> patientDrugDtos) {
        return ResponseEntity.ok(patientDrugService.assignManyDrugsToPatientVisit(patientDrugDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDrugDto> getPatientDrugById(@PathVariable Long id) {
        return ResponseEntity.ok(patientDrugService.getPatientDrugById(id));
    }

    @GetMapping("/visit/{patientVisitId}")
    public ResponseEntity<List<PatientDrugDto>> getDrugsForPatientVisit(@PathVariable Long patientVisitId) {
        return ResponseEntity.ok(patientDrugService.getDrugsForPatientVisit(patientVisitId));
    }

    @GetMapping("/visit/{patientVisitId}/paged")
    public ResponseEntity<ListFetchDto<PatientDrugDto>> getDrugsForPatientVisitPaged(
            @PathVariable Long patientVisitId,
            Pageable pageable) {
        return ResponseEntity.ok(patientDrugService.getDrugsForPatientVisit(patientVisitId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDrugDto> updatePatientDrug(
            @PathVariable Long id,
            @RequestBody PatientDrugDto patientDrugDto) {
        return ResponseEntity.ok(patientDrugService.updatePatientDrug(id, patientDrugDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientDrug(@PathVariable Long id) {
        patientDrugService.deletePatientDrug(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/dispense")
    public ResponseEntity<PatientDrugDto> dispenseDrug(@PathVariable Long id) {
        return ResponseEntity.ok(patientDrugService.dispenseDrug(id));
    }

    @GetMapping("/visit/{patientVisitId}/dispensed")
    public ResponseEntity<List<PatientDrugDto>> getDispensedDrugsForPatientVisit(@PathVariable Long patientVisitId) {
        return ResponseEntity.ok(patientDrugService.getDispensedDrugsForPatientVisit(patientVisitId));
    }

    @GetMapping("/visit/{patientVisitId}/undispensed")
    public ResponseEntity<List<PatientDrugDto>> getUndispensedDrugsForPatientVisit(@PathVariable Long patientVisitId) {
        return ResponseEntity.ok(patientDrugService.getUndispensedDrugsForPatientVisit(patientVisitId));
    }
}
