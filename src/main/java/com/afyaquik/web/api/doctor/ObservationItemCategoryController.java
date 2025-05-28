package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.ObservationItemCategoryDto;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observation/items/categories")
@RequiredArgsConstructor
public class ObservationItemCategoryController {
    private final ObservationService observationService;

    @PostMapping
    public ResponseEntity<ObservationItemCategoryDto> addObservationItemCategory(@RequestBody ObservationItemCategoryDto observationItemCategoryDto) {
        return ResponseEntity.ok(observationService.addObservationItemCategory(observationItemCategoryDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ObservationItemCategoryDto> updateObservationItemCategory(@PathVariable Long id,@RequestBody ObservationItemCategoryDto observationItemCategoryDto) {
        return ResponseEntity.ok(observationService.updateObservationItemCategory(id, observationItemCategoryDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ObservationItemCategoryDto> getObservationItemCategory(@PathVariable Long id) {
        return ResponseEntity.ok(observationService.getObservationItemCategory(id));
    }
    @GetMapping
    public ResponseEntity<ListFetchDto<ObservationItemCategoryDto>> getAllObservationItemCategories(Pageable pageable) {
        return ResponseEntity.ok(observationService.getObservationItemCategories(pageable));
    }
    @DeleteMapping("/{id}")
    public void deleteObservationItemCategory(@PathVariable Long id) {
        observationService.deleteObservationItemCategory(id);
        ResponseEntity.ok();
    }

}
