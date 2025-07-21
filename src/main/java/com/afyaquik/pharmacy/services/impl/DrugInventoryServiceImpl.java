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
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static com.afyaquik.utils.BatchNumberGenerator.generateBatchNumberWithDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugInventoryServiceImpl implements DrugInventoryService {
    private final DrugInventoryRepository drugInventoryRepository;
    private final DrugInventoryMapper drugInventoryMapper;
    private final DrugRepository drugRepository;
    @Override
    @PreAuthorize("hasAnyRole('PHARMACY', 'ADMIN', 'SUPERADMIN')")
    @Transactional
    public DrugInventoryDto adjustInventory(Long drugId, String batchNumber, double quantity) {
        // quantity can be negative (to reduce inventory) or positive (to increase inventory)
        //inventory must be active
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        DrugInventory drugInventory = drugInventoryRepository.findByDrugAndBatchNumberAndActiveTrue(drug, batchNumber)
                .orElseThrow(() -> new EntityNotFoundException("Active Drug inventory not found for drug id: " + drugId + " and batch number: " + batchNumber));
        if (quantity < 0 && Math.abs(quantity) > drugInventory.getCurrentQuantity()) {
            throw new DrugServiceException("Cannot reduce inventory below zero for drug id: " + drugId + " and batch number: " + batchNumber);
        }
        if (quantity>0)
        {
            throw new DrugServiceException("Cannot increase inventory here. Ask pharmacy manager to add a new inventory.");
        }
        drugInventory.setCurrentQuantity(drugInventory.getCurrentQuantity() + quantity);
        drugInventory.setActive(drugInventory.getCurrentQuantity() != 0);
        DrugInventory updatedInventory = drugInventoryRepository.save(drugInventory);
        return drugInventoryMapper.toDto(updatedInventory);
    }

    @Override
    public boolean isDrugInStock(Long drugId) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return drugInventoryRepository.existsByDrugAndCurrentQuantityGreaterThanAndActiveTrue(drug, 0);
    }

    @Override
    public double getCurrentInventoryCountForDrug(Long drugId) {
        drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return drugInventoryRepository.findByDrugIdAndActiveTrue(drugId).stream()
                .mapToDouble(DrugInventory::getCurrentQuantity)
                .sum();
    }

    @Override
    public DrugInventoryDto getDrugInventoryById(Long id) {
        return drugInventoryRepository.findById(id)
                .map(drugInventoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found with id: " + id));
    }

    @Override
    public DrugInventoryDto getDrugInventoryByBatchNumber(Long drugId, String batchNumber) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return drugInventoryRepository.findByDrugAndBatchNumberAndActiveTrue(drug, batchNumber)
                .map(drugInventoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found for drug id: " + drugId + " and batch number: " + batchNumber));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @Transactional
    public DrugInventoryDto updateDrugInventory(Long id, DrugInventoryDto drugInventoryDto) {
        DrugInventory existingInventory = drugInventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found with id: " + id));

       drugRepository.findById(drugInventoryDto.getDrugId())
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugInventoryDto.getDrugId()));

        existingInventory.setExpiryDate(drugInventoryDto.getExpiryDate());
        existingInventory.setReceivedDate(drugInventoryDto.getReceivedDate());
        existingInventory.setSupplierName(drugInventoryDto.getSupplierName());
        existingInventory.setBuyingPrice(drugInventoryDto.getBuyingPrice());
        existingInventory.setSellingPrice(drugInventoryDto.getSellingPrice());
        //to avoid fraudulent updates, we do not allow changing the batch number or current quantity directly
        // existingInventory.setCurrentQuantity(drugInventoryDto.getCurrentQuantity());
        //existingInventory.setBatchNumber(drugInventoryDto.getBatchNumber());

        existingInventory.setActive(drugInventoryDto.isActive());

        DrugInventory updatedInventory = drugInventoryRepository.save(existingInventory);
        double totalDrugStock = drugInventoryRepository.findByDrugIdAndActiveTrue(existingInventory.getDrug().getId())
                .stream()
                .mapToDouble(DrugInventory::getCurrentQuantity)
                .sum();
        Drug drug = existingInventory.getDrug();
        drug.setStockQuantity(totalDrugStock);
        drugRepository.save(drug);
        return drugInventoryMapper.toDto(updatedInventory);
    }

    @Override
    public void deleteDrugInventory(Long id) {
        DrugInventory drugInventory = drugInventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug inventory not found with id: " + id));
        drugInventoryRepository.delete(drugInventory);
    }

    @Override
    @Transactional
    public DrugInventoryDto addNewInventory(DrugInventoryDto drugInventoryDto) {
        Drug drug = drugRepository.findById(drugInventoryDto.getDrugId())
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugInventoryDto.getDrugId()));

        if (drugInventoryRepository.findByBatchNumberAndBatchNumberNotNull(drugInventoryDto.getBatchNumber()).isPresent()) {
            throw new DrugServiceException("Batch number already exists: " + drugInventoryDto.getBatchNumber());
        }

        DrugInventory newInventory = DrugInventory.builder()
                .build();
        newInventory.setDrug(drug);
        newInventory.setExpiryDate(drugInventoryDto.getExpiryDate());
        newInventory.setReceivedDate(drugInventoryDto.getReceivedDate());
        newInventory.setInitialQuantity(drugInventoryDto.getInitialQuantity());
        newInventory.setCurrentQuantity(drugInventoryDto.getInitialQuantity());
        newInventory.setBuyingPrice(drugInventoryDto.getBuyingPrice());
        newInventory.setSellingPrice(drugInventoryDto.getSellingPrice());

        // Set the batch number to a generated value if not provided
        if (newInventory.getBatchNumber() == null || newInventory.getBatchNumber().isEmpty()) {

            String batchNumber;
            do {
                batchNumber = generateBatchNumberWithDate();
            }while (drugInventoryRepository.findByBatchNumberAndBatchNumberNotNull(batchNumber).isPresent());
            newInventory.setBatchNumber(batchNumber);
        }
        else
        {
            // Validate the provided batch number format
            if (drugInventoryDto.getBatchNumber().length()<6 || !drugInventoryDto.getBatchNumber().matches("^[A-Z0-9]+$")) {
                throw new DrugServiceException("Invalid batch number format. Must be alphanumeric and at least 6 characters long.");
            }
            newInventory.setBatchNumber(drugInventoryDto.getBatchNumber());
        }
        newInventory.setActive(newInventory.getCurrentQuantity() > 0);
        DrugInventory savedInventory = drugInventoryRepository.save(newInventory);
        double totalDrugStock = drugInventoryRepository.findByDrugIdAndActiveTrue(savedInventory.getDrug().getId())
                .stream()
                .mapToDouble(DrugInventory::getCurrentQuantity)
                .sum();

        drug.setStockQuantity(totalDrugStock);
        drugRepository.save(drug);
        return drugInventoryMapper.toDto(savedInventory);
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllDrugInventories(Pageable pageable) {
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAll(pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllNonEmptyBatchInventoriesForDrugId(Long drugId, Pageable pageable) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAllByDrugAndCurrentQuantityGreaterThanAndActiveTrue(drug, 0, pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllActiveInventories(Pageable pageable) {
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAllByActiveTrue(pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllExpiredActiveInventories(Pageable pageable) {
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAllByExpiryDateBeforeAndActiveTrue(java.time.LocalDate.now(), pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllInventoriesByDrugId(Long drugId, Pageable pageable) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAllByDrug(drug, pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<DrugInventoryDto> getAllActiveInventoriesByDrugId(Long drugId, Pageable pageable) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + drugId));
        return ListFetchDto.<DrugInventoryDto>builder()
                .results(drugInventoryRepository.findAllByDrugAndActiveTrue(drug, pageable).map(drugInventoryMapper::toDto))
                .build();
    }

    @Override
    public void deactivateExpiredDrugs() {
        java.time.LocalDate today = java.time.LocalDate.now();
        drugInventoryRepository.findAllByExpiryDateBeforeAndActiveTrue(today, Pageable.unpaged())
                .forEach(drugInventory -> {
                    drugInventory.setActive(false);
                    log.info("Deactivating expired drug inventory: {} with batch number: {}", drugInventory.getId(), drugInventory.getBatchNumber());
                    drugInventoryRepository.save(drugInventory);
                });
    }
}
