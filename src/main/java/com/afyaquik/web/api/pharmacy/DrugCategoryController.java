package com.afyaquik.web.api.pharmacy;

import com.afyaquik.pharmacy.dto.DrugCategoryDto;
import com.afyaquik.pharmacy.entity.DrugCategory;
import com.afyaquik.pharmacy.services.DrugCategoryService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugs/categories")
@RequiredArgsConstructor
public class DrugCategoryController {
    private final DrugCategoryService drugCategoryService;
    @GetMapping
    public ResponseEntity<ListFetchDto<DrugCategoryDto>> getAllDrugCategories(Pageable pageable) {
        return ResponseEntity.ok(drugCategoryService.getAllDrugCategories(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DrugCategoryDto> getDrugCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(drugCategoryService.getDrugCategoryById(id));
    }
    @PostMapping
    public ResponseEntity<DrugCategoryDto> createDrugCategory(@RequestBody DrugCategoryDto drugCategoryDto) {
        return ResponseEntity.ok(drugCategoryService.createDrugCategory(drugCategoryDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<DrugCategoryDto> updateDrugCategory(@PathVariable Long id, @RequestBody DrugCategoryDto drugCategoryDto) {
        return ResponseEntity.ok(drugCategoryService.updateDrugCategory(id, drugCategoryDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrugCategory(@PathVariable Long id) {
        drugCategoryService.deleteDrugCategory(id);
        return ResponseEntity.ok().build();
    }
}
