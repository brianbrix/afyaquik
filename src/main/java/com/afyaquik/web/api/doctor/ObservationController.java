package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
public class ObservationController {
    private final ObservationService observationService;
    @PostMapping("/add")
    public ResponseEntity<ObservationReportDto> addObservation(@RequestBody ObservationReportDto  observationReportDto) {
        return ResponseEntity.ok(observationService.addObservationReport(observationReportDto));
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<ObservationReportDto> updateObservation(@PathVariable Long id, @RequestBody ObservationReportDto observationReportDto) {
        return ResponseEntity.ok(observationService.updateObservationReport(id,observationReportDto));
    }
    @PostMapping("/{id}/delete")
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
    public ResponseEntity<ListFetchDto<ObservationReportDto>> getPatientReports(@PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(observationService.getPatientReports(patientId, pageable));
    }
}
