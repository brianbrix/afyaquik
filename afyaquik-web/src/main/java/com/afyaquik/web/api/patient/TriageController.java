package com.afyaquik.web.api.patient;

import com.afyaquik.dtos.patient.TriageItemDto;
import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.dtos.patient.TriageReportItemDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.patients.entity.TriageItem;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.services.TriageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/triage")
@RequiredArgsConstructor
@PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class TriageController {
    private final TriageService triageService;

    @PutMapping("/{visitId}/update")
    public ResponseEntity<TriageReportDto> updateTriage(@RequestBody List<TriageReportItemDto> triageReportItemDtos, @PathVariable Long visitId) {
        return ResponseEntity.ok(triageService.updateTriageReport(visitId,triageReportItemDtos));
    }
    @GetMapping("/{visitId}")
    public ResponseEntity<ListFetchDto<TriageReportItemDto>> getTriageReport(@PathVariable Long visitId, Pageable pageable) {
        return ResponseEntity.ok(triageService.getTriageReportItemsForVisit(visitId, pageable));
    }
    @GetMapping("/items")
    public ResponseEntity<List<TriageItemDto>> getTriageItems() {
        return ResponseEntity.ok(triageService.getTriageItems());
    }
    @PostMapping("/items")
    public ResponseEntity<TriageItemDto> createTriageItem(@Validated @RequestBody TriageItemDto triageItemDto) {
        return ResponseEntity.ok(triageService.createTriageItem(triageItemDto));
    }
    @PutMapping("/items/{id}")
    public ResponseEntity<TriageItemDto> updateTriageItem(@PathVariable Long id, @Validated @RequestBody TriageItemDto triageItemDto) {
        return ResponseEntity.ok(triageService.updateTriageItem(id, triageItemDto));
    }
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteTriageItem(@PathVariable Long id) {
        triageService.deleteTriageItem(id);
        return ResponseEntity.ok(null);
    }
    @GetMapping("/items/{itemId}")
    public ResponseEntity<TriageItemDto> getTriageItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(triageService.getTriageItem(itemId));
    }
}
