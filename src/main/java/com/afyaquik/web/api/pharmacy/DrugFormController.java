package com.afyaquik.web.api.pharmacy;

import com.afyaquik.pharmacy.dto.DrugFormDto;
import com.afyaquik.pharmacy.services.DrugFormService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugs/forms")
@RequiredArgsConstructor
public class DrugFormController {
    private final DrugFormService drugFormService;
    @PostMapping
    public ResponseEntity<DrugFormDto> createDrugForm(@RequestBody DrugFormDto drugFormDto) {
        return ResponseEntity.ok(drugFormService.createDrugForm(drugFormDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DrugFormDto> getDrugFormById(@PathVariable Long id) {
        return ResponseEntity.ok(drugFormService.getDrugFormById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<DrugFormDto> updateDrugForm(@PathVariable Long id, @RequestBody DrugFormDto drugFormDto) {
        return ResponseEntity.ok(drugFormService.updateDrugForm(id, drugFormDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrugForm(@PathVariable Long id) {
        drugFormService.deleteDrugForm(id);
        return ResponseEntity.ok().build();
    }
}
