package com.afyaquik.pharmacy.services.impl;

import com.afyaquik.billing.dto.BillingDetailDto;
import com.afyaquik.billing.dto.BillingDto;
import com.afyaquik.billing.entity.BillingItem;
import com.afyaquik.billing.repository.BillingItemRepository;
import com.afyaquik.billing.service.BillingService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.pharmacy.dto.PatientDrugDto;
import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.pharmacy.entity.DrugInventory;
import com.afyaquik.pharmacy.entity.PatientDrug;
import com.afyaquik.pharmacy.repository.DrugInventoryRepository;
import com.afyaquik.pharmacy.repository.DrugRepository;
import com.afyaquik.pharmacy.repository.PatientDrugRepository;
import com.afyaquik.pharmacy.services.DrugInventoryService;
import com.afyaquik.pharmacy.services.PatientDrugService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.exceptions.DrugServiceException;
import com.afyaquik.utils.mappers.pharmacy.PatientDrugMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientDrugServiceImpl implements PatientDrugService {
    private final PatientDrugRepository patientDrugRepository;
    private final PatientVisitRepo patientVisitRepo;
    private final DrugRepository drugRepository;
    private final PatientDrugMapper patientDrugMapper;
    private final DrugInventoryService drugInventoryService;
    private final DrugInventoryRepository drugInventoryRepository;
    private final BillingService billingService;
    private final BillingItemRepository billingItemRepository;

    @Override
    @Transactional
    public PatientDrugDto assignDrugToPatientVisit(PatientDrugDto patientDrugDto) {
        PatientVisit patientVisit = patientVisitRepo.findById(patientDrugDto.getPatientVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + patientDrugDto.getPatientVisitId()));

        Drug drug = drugRepository.findById(patientDrugDto.getDrugId())
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + patientDrugDto.getDrugId()));

        PatientDrug patientDrug = PatientDrug.builder()
                .patientVisit(patientVisit)
                .drug(drug)
                .quantity(patientDrugDto.getQuantity())
                .dosageInstructions(patientDrugDto.getDosageInstructions())
                .dispensed(patientDrugDto.isDispensed())
                .build();

        // Update the pharmacy billing detail
//        updatePharmacyBillingDetail(patientVisit);

        return patientDrugMapper.toDto(patientDrugRepository.save(patientDrug));
    }

    @Override
    @Transactional
    public List<PatientDrugDto> assignManyDrugsToPatientVisit(List<PatientDrugDto> patientDrugDtos) {
        if (patientDrugDtos.isEmpty()) {
            return List.of();
        }

        // Get the patient visit ID from the first drug
        Long patientVisitId = patientDrugDtos.get(0).getPatientVisitId();

        // Assign all drugs to the patient visit
        List<PatientDrugDto> assignedDrugs = patientDrugDtos.stream()
                .map(dto -> {
                    PatientVisit patientVisit = patientVisitRepo.findById(dto.getPatientVisitId())
                            .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + dto.getPatientVisitId()));

                    Drug drug = drugRepository.findById(dto.getDrugId())
                            .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + dto.getDrugId()));

                    PatientDrug patientDrug = PatientDrug.builder()
                            .patientVisit(patientVisit)
                            .drug(drug)
                            .quantity(dto.getQuantity())
                            .dosageInstructions(dto.getDosageInstructions())
                            .dispensed(dto.isDispensed())
                            .build();

                    return patientDrugMapper.toDto(patientDrugRepository.save(patientDrug));
                })
                .collect(Collectors.toList());

        // Update the pharmacy billing detail
        PatientVisit patientVisit = patientVisitRepo.findById(patientVisitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found with id: " + patientVisitId));
//        updatePharmacyBillingDetail(patientVisit);

        return assignedDrugs;
    }

    @Override
    public PatientDrugDto getPatientDrugById(Long id) {
        return patientDrugRepository.findById(id)
                .map(patientDrugMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Patient drug not found with id: " + id));
    }

    @Override
    public List<PatientDrugDto> getDrugsForPatientVisit(Long patientVisitId) {
        return patientDrugRepository.findByPatientVisitId(patientVisitId)
                .stream()
                .map(patientDrugMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ListFetchDto<PatientDrugDto> getDrugsForPatientVisit(Long patientVisitId, Pageable pageable) {
        // This is a simplified implementation. In a real-world scenario, you might want to use a custom repository method
        // that returns a Page of PatientDrug entities filtered by patientVisitId.
        List<PatientDrugDto> drugs = getDrugsForPatientVisit(patientVisitId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), drugs.size());

        Page<PatientDrugDto> page = new org.springframework.data.domain.PageImpl<>(
                drugs.subList(start, end), pageable, drugs.size());

        return ListFetchDto.<PatientDrugDto>builder()
                .results(page)
                .build();
    }

    @Override
    @Transactional
    public PatientDrugDto updatePatientDrug(Long id, PatientDrugDto patientDrugDto) {
        PatientDrug existingPatientDrug = patientDrugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient drug not found with id: " + id));

        // Update only the fields that can be updated
        existingPatientDrug.setQuantity(patientDrugDto.getQuantity());
        existingPatientDrug.setDosageInstructions(patientDrugDto.getDosageInstructions());
        existingPatientDrug.setDispensed(patientDrugDto.isDispensed());

        // Update the pharmacy billing detail
//        updatePharmacyBillingDetail(existingPatientDrug.getPatientVisit());

        return patientDrugMapper.toDto(patientDrugRepository.save(existingPatientDrug));
    }

    @Override
    @Transactional
    public void deletePatientDrug(Long id) {
        PatientDrug patientDrug = patientDrugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient drug not found with id: " + id));

        PatientVisit patientVisit = patientDrug.getPatientVisit();

        patientDrugRepository.delete(patientDrug);

        // Update the pharmacy billing detail
        updatePharmacyBillingDetail(patientVisit);
    }

    @Override
    @Transactional
    public PatientDrugDto dispenseDrug(Long id) {
        PatientDrug patientDrug = patientDrugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient drug not found with id: " + id));

        // Only update inventory if the drug wasn't already dispensed
        if (!patientDrug.isDispensed()) {
            Drug drug = patientDrug.getDrug();
            double quantityToDispense = patientDrug.getQuantity();

            // Find active inventory entries for this drug
            List<DrugInventory> activeInventories = drugInventoryRepository.findByDrugIdAndActiveTrue(drug.getId()).stream()
                    .filter(DrugInventory::isActive)
                    .filter(inventory -> inventory.getCurrentQuantity() > 0)
                    .toList();

            if (activeInventories.isEmpty()) {
                throw new EntityNotFoundException("No active inventory found for drug: " + drug.getName());
            }

            double remainingQuantity = quantityToDispense;

            // Dispense from each inventory batch until the required quantity is met
            for (DrugInventory inventory : activeInventories) {
                if (remainingQuantity <= 0) {
                    break;
                }

                double quantityFromThisBatch = Math.min(remainingQuantity, inventory.getCurrentQuantity());

                // Adjust inventory for this batch
                drugInventoryService.adjustInventory(drug.getId(), inventory.getBatchNumber(), -quantityFromThisBatch);

                remainingQuantity -= quantityFromThisBatch;
            }

            if (remainingQuantity > 0) {
                throw new DrugServiceException("Not enough inventory to dispense drug: " + drug.getName());
            }
        }

        patientDrug.setDispensed(true);

        PatientDrugDto dispensedPatientDrugDto = patientDrugMapper.toDto(patientDrugRepository.save(patientDrug));

        // Update the pharmacy billing detail
        updatePharmacyBillingDetail(patientDrug.getPatientVisit());

        return dispensedPatientDrugDto;
    }

    @Override
    public List<PatientDrugDto> getDispensedDrugsForPatientVisit(Long patientVisitId) {
        return patientDrugRepository.findByPatientVisitIdAndDispensed(patientVisitId, true)
                .stream()
                .map(patientDrugMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientDrugDto> getUndispensedDrugsForPatientVisit(Long patientVisitId) {
        return patientDrugRepository.findByPatientVisitIdAndDispensed(patientVisitId, false)
                .stream()
                .map(patientDrugMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates the Pharmacy billing detail for a patient visit with the total price of all drugs assigned to the visit.
     * If a Pharmacy billing detail doesn't exist, it creates one.
     *
     * @param patientVisit the patient visit
     */
    private void updatePharmacyBillingDetail(PatientVisit patientVisit) {
        // Get all drugs assigned to the patient visit
        List<PatientDrug> patientDrugs = patientDrugRepository.findByPatientVisitId(patientVisit.getId());

        // If there are no drugs, return
        if (patientDrugs.isEmpty()) {
            return;
        }

        // Calculate the total price of all drugs
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PatientDrug patientDrug : patientDrugs) {
            Drug drug = patientDrug.getDrug();
            // Get the active inventory for this drug to get the selling price
            List<DrugInventory> activeInventories = drugInventoryRepository.findByDrugIdAndActiveTrue(drug.getId()).stream()
                    .filter(DrugInventory::isActive)
                    .toList();

            if (!activeInventories.isEmpty()) {
                // Use the first active inventory's selling price
                DrugInventory inventory = activeInventories.get(0);
                BigDecimal drugPrice = BigDecimal.valueOf(inventory.getSellingPrice());
                BigDecimal quantity = BigDecimal.valueOf(patientDrug.getQuantity());
                totalPrice = totalPrice.add(drugPrice.multiply(quantity));
            }
        }

        // Get or create the billing for this patient visit
        BillingDto billingDto;
        try {
            billingDto = billingService.getBillingByPatientVisitId(patientVisit.getId());
        } catch (EntityNotFoundException e) {
            // Create a new billing if one doesn't exist
            billingDto = billingService.createBilling(BillingDto.builder()
                    .patientVisitId(patientVisit.getId())
                    .amount(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .description("Billing for patient visit")
                    .build());
        }

        // Find the Pharmacy billing item
        BillingItem pharmacyBillingItem = billingItemRepository.findByNameIgnoreCase("Pharmacy")
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy billing item not found"));

        // Check if a Pharmacy billing detail already exists
        List<BillingDetailDto> billingDetails = billingService.getBillingDetailsByBillingId(billingDto.getId());
        BillingDetailDto pharmacyBillingDetailDto = null;

        for (BillingDetailDto billingDetailDto : billingDetails) {
            if (billingDetailDto.getBillingItemId().equals(pharmacyBillingItem.getId())) {
                pharmacyBillingDetailDto = billingDetailDto;
                break;
            }
        }

        if (pharmacyBillingDetailDto != null) {
            // Update the existing Pharmacy billing detail
            pharmacyBillingDetailDto.setAmount(totalPrice);
            pharmacyBillingDetailDto.setQuantity(1);
            pharmacyBillingDetailDto.setDescription("Pharmacy drugs for patient visit");
            billingService.updateBillingDetail(pharmacyBillingDetailDto.getId(), pharmacyBillingDetailDto);
        } else {
            // Create a new Pharmacy billing detail
            BillingDetailDto newBillingDetailDto = BillingDetailDto.builder()
                    .billingId(billingDto.getId())
                    .billingItemId(pharmacyBillingItem.getId())
                    .amount(totalPrice)
                    .quantity(1)
                    .description("Pharmacy drugs for patient visit")
                    .build();

            billingService.addBillingDetail(billingDto.getId(), newBillingDetailDto);
        }
    }
}
