package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanItemDto;
import com.afyaquik.doctor.service.TreatmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'DOCTOR')")
@RequestMapping("/api/plan/items")
@RequiredArgsConstructor
public class TreatmentPlanItemController {
    private final TreatmentPlanService treatmentPlanService;
    @PostMapping
    public ResponseEntity<TreatmentPlanItemDto> addTreatmentPlanItem(@RequestBody TreatmentPlanItemDto treatmentPlanItemDto) {
        return ResponseEntity.ok(treatmentPlanService.addTreatmentPlanItem(treatmentPlanItemDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TreatmentPlanItemDto> getTreatmentPlanItem(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlanItem(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TreatmentPlanItemDto> updateTreatmentPlanItem(@PathVariable Long id, @RequestBody TreatmentPlanItemDto treatmentPlanItemDto) {
        return ResponseEntity.ok(treatmentPlanService.updateTreatmentPlanItem(id, treatmentPlanItemDto));
    }
    @DeleteMapping("/{id}")
    public void deleteTreatmentPlanItem(@PathVariable Long id) {
        treatmentPlanService.deleteTreatmentPlanItem(id);
        ResponseEntity.ok();
    }

}
