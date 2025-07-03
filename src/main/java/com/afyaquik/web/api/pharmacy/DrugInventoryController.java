package com.afyaquik.web.api.pharmacy;

import com.afyaquik.pharmacy.dto.DrugInventoryDto;
import com.afyaquik.pharmacy.services.DrugInventoryService;
import com.afyaquik.utils.dto.search.ListFetchDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugs/inventory")
@RequiredArgsConstructor
public class DrugInventoryController {
    private final DrugInventoryService drugInventoryService;
    @PostMapping
    ResponseEntity<DrugInventoryDto >addDrugInventory( @Validated @RequestBody DrugInventoryDto drugInventoryDto) {
        return ResponseEntity.ok(drugInventoryService.addNewInventory(drugInventoryDto));
    }
    @PutMapping("/{id}")
    ResponseEntity<DrugInventoryDto> updateDrugInventory(@PathVariable Long id, @RequestBody DrugInventoryDto drugInventoryDto) {
        return ResponseEntity.ok(drugInventoryService.updateDrugInventory(id,drugInventoryDto));
    }
    @GetMapping("/{id}")
    ResponseEntity<DrugInventoryDto> getDrugInventory(@PathVariable Long id) {
        return ResponseEntity.ok(drugInventoryService.getDrugInventoryById(id));
    }
    @DeleteMapping("/{id}")
            ResponseEntity<Void> deleteDrugInventory(@PathVariable Long id) {
        drugInventoryService.deleteDrugInventory(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/batch/{drugId}/{batchNumber}")
    ResponseEntity<DrugInventoryDto> getDrugInventoryByBatchNumber(@PathVariable Long drugId, @PathVariable String batchNumber) {
        return ResponseEntity.ok(drugInventoryService.getDrugInventoryByBatchNumber(drugId, batchNumber));
    }
    @PostMapping("/adjust/{drugId}/{batchNumber}/{quantity}")
    ResponseEntity<DrugInventoryDto> adjustInventory(@PathVariable Long drugId, @PathVariable String batchNumber, @PathVariable int quantity) {
        return ResponseEntity.ok(drugInventoryService.adjustInventory(drugId, batchNumber, quantity));
    }
    @GetMapping("/in-stock/{drugId}")
    ResponseEntity<Boolean> isDrugInStock(@PathVariable Long drugId) {
        return ResponseEntity.ok(drugInventoryService.isDrugInStock(drugId));
    }
    @GetMapping("/current-count/{drugId}")
    ResponseEntity<Integer> getCurrentInventoryCountForDrug(@PathVariable Long drugId) {
        return ResponseEntity.ok(drugInventoryService.getCurrentInventoryCountForDrug(drugId));
    }
    @GetMapping
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllDrugInventories(Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllDrugInventories(pageable));
    }
    @GetMapping("/non-empty-batches/{drugId}")
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllNonEmptyBatchInventoriesForDrugId(@PathVariable Long drugId, Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllNonEmptyBatchInventoriesForDrugId(drugId, pageable));
    }
    @GetMapping("/active")
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllActiveInventories(Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllActiveInventories(pageable));
    }
    @GetMapping("/expired")
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllExpiredActiveInventories(Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllExpiredActiveInventories(pageable));
    }
    @GetMapping("/by-drug/{drugId}")
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllInventoriesByDrugId(@PathVariable Long drugId, Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllInventoriesByDrugId(drugId, pageable));
    }
    @GetMapping("/active-by-drug/{drugId}")
    ResponseEntity<ListFetchDto<DrugInventoryDto>> getAllActiveInventoriesByDrugId(@PathVariable Long drugId, Pageable pageable) {
        return ResponseEntity.ok(drugInventoryService.getAllActiveInventoriesByDrugId(drugId, pageable));
    }
    @PostMapping("/deactivate-expired")
    ResponseEntity<Void> deactivateExpiredDrugs() {
        drugInventoryService.deactivateExpiredDrugs();
        return ResponseEntity.ok().build();
    }
}
