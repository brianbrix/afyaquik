package com.afyaquik.web.api.pharmacy;

import com.afyaquik.pharmacy.dto.DrugDto;
import com.afyaquik.pharmacy.services.DrugService;
import com.afyaquik.utils.dto.search.ListFetchDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {
    private final DrugService drugService;

    @GetMapping
    public ResponseEntity<ListFetchDto<DrugDto>> getAllDrugs(Pageable pageable, @RequestParam(required = false) Boolean enabled) {
        ListFetchDto<DrugDto> drugs;
        if (enabled != null) {
            drugs= drugService.getDrugsByEnabled(pageable, enabled);
        }
        else {
            drugs = drugService.getAllDrugs(pageable);
        }
        return ResponseEntity.ok(drugs);
    }

    @PostMapping
    public ResponseEntity<DrugDto> createDrug(@RequestBody DrugDto drugDto) {
        DrugDto createdDrug = drugService.createDrug(drugDto);
        return ResponseEntity.ok(createdDrug);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DrugDto> getDrugById(@PathVariable Long id) {
        DrugDto drug = drugService.getDrugById(id);
        return ResponseEntity.ok(drug);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DrugDto> updateDrug(@PathVariable Long id, @RequestBody DrugDto drugDto) {
        DrugDto updatedDrug = drugService.updateDrug(id, drugDto);
        return ResponseEntity.ok(updatedDrug);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Long id) {
        drugService.deleteDrug(id);
        return ResponseEntity.ok().build();
    }

}
