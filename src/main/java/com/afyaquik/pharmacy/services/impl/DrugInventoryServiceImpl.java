package com.afyaquik.pharmacy.services.impl;

import com.afyaquik.pharmacy.dto.DrugInventoryDto;
import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.pharmacy.entity.DrugInventory;
import com.afyaquik.pharmacy.repository.DrugInventoryRepository;
import com.afyaquik.pharmacy.repository.DrugRepository;
import com.afyaquik.pharmacy.services.DrugInventoryService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.exceptions.DrugServiceException;
import com.afyaquik.utils.mappers.pharmacy.DrugInventoryMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.afyaquik.utils.BatchNumberGenerator.generateBatchNumberWithDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugInventoryServiceImpl implements DrugInventoryService {
    private static final int MIN_BATCH_NUMBER_LENGTH = 6;
    private static final String BATCH_NUMBER_REGEX = "^[A-Z0-9]+$";

    private final DrugInventoryRepository drugInventoryRepository;
    private final DrugInventoryMapper drugInventoryMapper;
    private final DrugRepository drugRepository;

    @Override
    @PreAuthorize("hasAnyRole('PHARMACY', 'ADMIN', 'SUPERADMIN')")
    @Transactional
    public DrugInventoryDto adjustInventory(Long drugId, String batchNumber, double quantity) {
        if (quantity > 0) {
            throw new DrugServiceException("Cannot increase inventory here. Ask pharmacy manager to add a new inventory.");
        }

        Drug drug = getDrugById(drugId);
        DrugInventory drugInventory = getActiveInventoryByDrugAndBatch(drug, batchNumber);

        if (Math.abs(quantity) > drugInventory.getCurrentQuantity()) {
            throw new DrugServiceException("Cannot reduce inventory below zero for drug id: " + drugId + " and batch number: " + batchNumber);
        }

        drugInventory.setCurrentQuantity(drugInventory.getCurrentQuantity() + quantity);
        drugInventory.setActive(drugInventory.getCurrentQuantity() > 0);

        DrugInventory updatedInventory = drugInventoryRepository.save(drugInventory);
        updateDrugStockQuantity(drug);

        return drugInventoryMapper.toDto(updatedInventory);
    }

    @Override
    public boolean isDrugInStock(Long drugId) {
        Drug drug = getDrugById(drugId);
        return drugInventoryRepository.existsByDrugAndCurrentQuantityGreaterThanAndActiveTrue(drug, 0);
    }

    @Override
    public double getCurrentInventoryCountForDrug(Long drugId) {
        getDrugById(drugId); // Just for validation
        return drugInventoryRepository.sumCurrentQuantityByDrugIdAndActiveTrue(drugId);
    }

    @Override
    public DrugInventoryDto getDrugInventoryById(Long id) {
        return drugInventoryRepository.findById(id)
                .map(drugInventoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found with id: " + id));
    }

    @Override
    public DrugInventoryDto getDrugInventoryByBatchNumber(Long drugId, String batchNumber) {
        Drug drug = getDrugById(drugId);
        return drugInventoryRepository.findByDrugAndBatchNumberAndActiveTrue(drug, batchNumber)
                .map(drugInventoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Drug inventory not found for drug id: " + drugId + " and batch number: " + batchNumber));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @Transactional
    public DrugInventoryDto updateDrugInventory(Long id, DrugInventoryDto drugInventoryDto) {
        DrugInventory existingInventory = getInventoryById(id);

        if (existingInventory.isLocked()) {
            throw new DrugServiceException("You cannot edit a locked inventory record");
        }

        getDrugById(drugInventoryDto.getDrugId()); // Validate drug exists

        updateInventoryFields(existingInventory, drugInventoryDto);

        if (!drugInventoryDto.isActive() && existingInventory.isActive()) {
            deactivateInventory(existingInventory);
        }

        existingInventory.setActive(drugInventoryDto.isActive());

        DrugInventory updatedInventory = drugInventoryRepository.save(existingInventory);
        updateDrugStockQuantity(existingInventory.getDrug());

        return drugInventoryMapper.toDto(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteDrugInventory(Long id) {
        DrugInventory drugInventory = getInventoryById(id);
        drugInventoryRepository.delete(drugInventory);
    }

    @Override
    @Transactional
    public DrugInventoryDto addNewInventory(DrugInventoryDto drugInventoryDto) {
        Drug drug = getDrugById(drugInventoryDto.getDrugId());
        validateBatchNumber(drugInventoryDto.getBatchNumber());

        DrugInventory newInventory = buildNewInventory(drug, drugInventoryDto);
        setBatchNumber(newInventory, drugInventoryDto.getBatchNumber());

        newInventory.setActive(newInventory.getCurrentQuantity() > 0);
        DrugInventory savedInventory = drugInventoryRepository.save(newInventory);

        if (newInventory.isActive()) {
            updateDrugStockAndPrice(drug, savedInventory);
        }

        return drugInventoryMapper.toDto(savedInventory);
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllDrugInventories(Pageable pageable) {
        return buildListFetchDto(drugInventoryRepository.findAll(pageable));
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllNonEmptyBatchInventoriesForDrugId(Long drugId, Pageable pageable) {
        Drug drug = getDrugById(drugId);
        return buildListFetchDto(drugInventoryRepository.findAllByDrugAndCurrentQuantityGreaterThanAndActiveTrue(drug, 0, pageable));
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllActiveInventories(Pageable pageable) {
        return buildListFetchDto(drugInventoryRepository.findAllByActiveTrue(pageable));
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllExpiredActiveInventories(Pageable pageable) {
        return buildListFetchDto(drugInventoryRepository.findAllByExpiryDateBeforeAndActiveTrue(LocalDate.now(), pageable));
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllInventoriesByDrugId(Long drugId, Pageable pageable) {
        Drug drug = getDrugById(drugId);
        return buildListFetchDto(drugInventoryRepository.findAllByDrug(drug, pageable));
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllActiveInventoriesByDrugId(Long drugId, Pageable pageable) {
        Drug drug = getDrugById(drugId);
        return buildListFetchDto(drugInventoryRepository.findAllByDrugAndActiveTrue(drug, pageable));
    }

    @Override
    @Transactional
    public void deactivateExpiredDrugs() {
        LocalDate today = LocalDate.now();
        List<DrugInventory> expiredDrugs = drugInventoryRepository
                .findAllByExpiryDateBeforeAndActiveTrue(today, Pageable.unpaged())
                .getContent();

        if (expiredDrugs.isEmpty()) {
            log.info("No expired drugs found to deactivate");
            return;
        }

        log.info("Found {} expired drug inventories to deactivate", expiredDrugs.size());
        expiredDrugs.forEach(this::processExpiredDrug);
    }

    private void processExpiredDrug(DrugInventory drugInventory) {
        try {
            log.info("Deactivating expired drug inventory: {} with batch number: {}",
                    drugInventory.getId(), drugInventory.getBatchNumber());

            deactivateInventory(drugInventory);
            drugInventory.setLocked(true);
            drugInventoryRepository.save(drugInventory);
        } catch (Exception e) {
            log.error("Failed to deactivate drug inventory {}: {}", drugInventory.getId(), e.getMessage());
            throw e;
        }
    }

    private void deactivateInventory(DrugInventory drugInventory) {
        if (drugInventory.isActive()) {
            adjustStockOnDeactivation(drugInventory);
            drugInventory.setActive(false);
        }
        drugInventoryRepository.save(drugInventory);
    }

    private void adjustStockOnDeactivation(DrugInventory drugInventory) {
        Drug drug = drugInventory.getDrug();
        if (drug != null) {
            drug.setStockQuantity(drug.getStockQuantity() - drugInventory.getCurrentQuantity());
            drugRepository.save(drug);
        }
    }

    // Helper methods
    private Drug getDrugById(Long drugId) {
        return drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
    }

    private DrugInventory getInventoryById(Long id) {
        return drugInventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found with id: " + id));
    }

    private DrugInventory getActiveInventoryByDrugAndBatch(Drug drug, String batchNumber) {
        return drugInventoryRepository.findByDrugAndBatchNumberAndActiveTrue(drug, batchNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Active Drug inventory not found for drug id: " + drug.getId() + " and batch number: " + batchNumber));
    }

    private void updateInventoryFields(DrugInventory inventory, DrugInventoryDto dto) {
        inventory.setExpiryDate(dto.getExpiryDate());
        inventory.setReceivedDate(dto.getReceivedDate());
        inventory.setSupplierName(dto.getSupplierName());
        inventory.setBuyingPrice(dto.getBuyingPrice());
        inventory.setSellingPrice(dto.getSellingPrice());
        inventory.setComment(dto.getComment());
        inventory.setLocked(dto.isLocked());
    }

    private void validateBatchNumber(String batchNumber) {
        if (batchNumber != null && drugInventoryRepository.findByBatchNumber(batchNumber).isPresent()) {
            throw new DrugServiceException("Batch number already exists: " + batchNumber);
        }
    }

    private DrugInventory buildNewInventory(Drug drug, DrugInventoryDto dto) {
        return DrugInventory.builder()
                .drug(drug)
                .comment(dto.getComment())
                .expiryDate(dto.getExpiryDate())
                .receivedDate(dto.getReceivedDate())
                .initialQuantity(dto.getInitialQuantity())
                .currentQuantity(dto.getInitialQuantity())
                .buyingPrice(dto.getBuyingPrice())
                .sellingPrice(dto.getSellingPrice())
                .build();
    }

    private void setBatchNumber(DrugInventory inventory, String batchNumber) {
        if (batchNumber == null || batchNumber.isEmpty()) {
            inventory.setBatchNumber(generateUniqueBatchNumber());
        } else {
            if (batchNumber.length() < MIN_BATCH_NUMBER_LENGTH || !batchNumber.matches(BATCH_NUMBER_REGEX)) {
                throw new DrugServiceException(
                        "Invalid batch number format. Must be alphanumeric and at least " + MIN_BATCH_NUMBER_LENGTH + " characters long.");
            }
            inventory.setBatchNumber(batchNumber);
        }
    }

    private String generateUniqueBatchNumber() {
        String batchNumber;
        do {
            batchNumber = generateBatchNumberWithDate();
        } while (drugInventoryRepository.findByBatchNumber(batchNumber).isPresent());
        return batchNumber;
    }

    private void updateDrugStockQuantity(Drug drug) {
        double totalStock = drugInventoryRepository.sumCurrentQuantityByDrugIdAndActiveTrue(drug.getId());
        drug.setStockQuantity(totalStock);
        drugRepository.save(drug);
    }

    private void updateDrugStockAndPrice(Drug drug, DrugInventory inventory) {
        updateDrugStockQuantity(drug);
        drug.setCurrentPrice(inventory.getSellingPrice());
        drugRepository.save(drug);
    }

    private ListFetchDto<DrugInventoryDto> buildListFetchDto(Page<DrugInventory> page) {
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(page.map(drugInventoryMapper::toDto))
                .build();
    }
}
