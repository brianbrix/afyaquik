package com.afyaquik.web.api.patient;

import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.TriageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient/triage")
@RequiredArgsConstructor
public class TriageController {
    private final TriageService triageService;
    @PostMapping("/create")
    public ResponseEntity<?> createTriage(@RequestBody TriageReportDto triageReportDto) {
        return ResponseEntity.ok(triageService.createTriageReport(triageReportDto));
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateTriage(@RequestBody TriageReportDto triageReportDto) {
        return ResponseEntity.ok(triageService.updateTriageReport(triageReportDto));
    }
    @GetMapping("/get")
    public ResponseEntity<?> getTriage(@RequestBody TriageReportDto triageReportDto) {
        return ResponseEntity.ok(triageService.updateTriageReport(triageReportDto));
    }
}
