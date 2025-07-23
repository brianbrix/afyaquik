package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.DrugInventoryDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface DrugInventoryService {
    DrugInventoryDto adjustInventory(Long drugId, String batchNumber, double quantity);//quantity can be negative/positive for adjustments
    boolean isDrugInStock(Long drugId);
    double getCurrentInventoryCountForDrug(Long drugId);
    DrugInventoryDto getDrugInventoryById(Long id);
    DrugInventoryDto getDrugInventoryByBatchNumber(Long drugId, String batchNumber);
    DrugInventoryDto updateDrugInventory(Long id, DrugInventoryDto drugInventoryDto);
    void deleteDrugInventory(Long id);
    DrugInventoryDto addNewInventory(DrugInventoryDto drugInventoryDto);
    ListFetchDto<DrugInventoryDto> getAllDrugInventories(Pageable pageable);
    ListFetchDto<DrugInventoryDto> getAllNonEmptyBatchInventoriesForDrugId(Long drugId, Pageable pageable);//get all batches with non-zero current quantity for a specific drug
    ListFetchDto<DrugInventoryDto> getAllActiveInventories(Pageable pageable); //get all active inventories (current quantity > 0) for all drugs
    ListFetchDto<DrugInventoryDto> getAllExpiredActiveInventories(Pageable pageable); //get all expired inventories (expiry date < today) for all drugs
    ListFetchDto<DrugInventoryDto> getAllInventoriesByDrugId(Long drugId, Pageable pageable); //get all inventories for a specific drug by drug id
    ListFetchDto<DrugInventoryDto> getAllActiveInventoriesByDrugId(Long drugId, Pageable pageable); //get all active inventories (current quantity > 0) for a specific drug by drug id

    void deactivateExpiredDrugs();
}
