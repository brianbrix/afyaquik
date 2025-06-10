package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.service.TreatmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class TreatmentPlanController {
    private final TreatmentPlanService treatmentPlanService;
    @PostMapping
    public ResponseEntity<TreatmentPlanDto> addTreatmentPlan(@RequestBody TreatmentPlanDto treatmentPlanDto) {
        return ResponseEntity.ok(treatmentPlanService.addTreatmentPlan(treatmentPlanDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TreatmentPlanDto> getTreatmentPlan(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlan(id));
    }
    @DeleteMapping("/{id}")
    public void deleteTreatmentPlan(@PathVariable Long id) {
        treatmentPlanService.deleteTreatmentPlan(id);
        ResponseEntity.ok();
    }
}
