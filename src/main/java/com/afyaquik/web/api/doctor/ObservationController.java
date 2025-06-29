package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class ObservationController {
    private final ObservationService observationService;
    @PostMapping("/add")
    public ResponseEntity<ObservationReportDto> addObservation(@Validated @RequestBody ObservationReportDto  observationReportDto) {
        return ResponseEntity.ok(observationService.addObservationReport(observationReportDto));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ObservationReportDto> updateObservation(@PathVariable Long id, @Validated @RequestBody ObservationReportDto observationReportDto) {
        return ResponseEntity.ok(observationService.updateObservationReport(id,observationReportDto));
    }
    @DeleteMapping("/{id}/delete")
    public void deleteObservation(@PathVariable Long id) {
        observationService.deleteObservationReport(id);
        ResponseEntity.ok();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ObservationReportDto> getObservation(@PathVariable Long id) {
        return ResponseEntity.ok(observationService.getObservationReport(id));
    }
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ObservationReportDto> getObservationItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(observationService.getObservationReport(itemId));
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ListFetchDto<ObservationReportDto>> getPatientObservationReports(@PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(observationService.getPatientReports(patientId, pageable));
    }
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<ListFetchDto<ObservationReportDto>> getPatientVisitObservationReports(@PathVariable Long visitId, Pageable pageable) {
        return ResponseEntity.ok(observationService.getPatientVisitReports(visitId, pageable));
    }
}
