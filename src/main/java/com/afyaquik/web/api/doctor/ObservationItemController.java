package com.afyaquik.web.api.doctor;

import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observation/items")
@RequiredArgsConstructor
public class ObservationItemController {
    private final ObservationService observationService;
    @PostMapping
    public ResponseEntity<ObservationItemDto> addObservationItem(@Validated @RequestBody ObservationItemDto  observationItemDto) {
        return ResponseEntity.ok(observationService.addObservationItem(observationItemDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ObservationItemDto> updateObservationItem(@Validated @RequestBody ObservationItemDto observationItemDto, @PathVariable Long id) {
        return ResponseEntity.ok(observationService.updateObservationItem(id, observationItemDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ObservationItemDto> getObservationItem(@PathVariable Long id) {
        return ResponseEntity.ok(observationService.getObservationItem(id));
    }
    @DeleteMapping("/{id}")
    public void deleteObservationItem(@PathVariable Long id) {
        observationService.deleteObservationItem(id);
        ResponseEntity.ok();
    }
    @GetMapping
    public ResponseEntity<ListFetchDto<ObservationItemDto>> getAllObservationItems(Pageable pageable) {
        return ResponseEntity.ok(observationService.getObservationItems(pageable));
    }

}
