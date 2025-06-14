package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.DrugInventoryDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface DrugInventoryService {
    DrugInventoryDto adjustInventory(Long drugId, String batchNumber, int quantity);//quantity can be negative/positive for adjustments
    boolean isDrugInStock(Long drugId);
    int getCurrentInventoryCount(Long drugId);
    DrugInventoryDto getDrugInventoryById(Long id);
    DrugInventoryDto getDrugInventoryByBatchNumber(Long drugId, String batchNumber);
    DrugInventoryDto updateDrugInventory(Long id, DrugInventoryDto drugInventoryDto);
    void deleteDrugInventory(Long id);
    DrugInventoryDto addNewInventory(DrugInventoryDto drugInventoryDto);
    ListFetchDto<DrugInventoryDto> getAllDrugInventories(Pageable pageable);
    ListFetchDto<DrugInventoryDto> getAllNonEmptyBatchInventoriesForDrugId(Long drugId, Pageable pageable);//get all batches with non-zero current quantity for a specific drug

}
