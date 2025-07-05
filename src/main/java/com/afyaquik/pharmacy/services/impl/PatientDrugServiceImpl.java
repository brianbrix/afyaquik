package com.afyaquik.pharmacy.services.impl;

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

        return patientDrugMapper.toDto(patientDrugRepository.save(patientDrug));
    }

    @Override
    public List<PatientDrugDto> assignManyDrugsToPatientVisit(List<PatientDrugDto> patientDrugDtos) {
        return patientDrugDtos.stream()
                .map(this::assignDrugToPatientVisit)
                .collect(Collectors.toList());
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

        return patientDrugMapper.toDto(patientDrugRepository.save(existingPatientDrug));
    }

    @Override
    @Transactional
    public void deletePatientDrug(Long id) {
        PatientDrug patientDrug = patientDrugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient drug not found with id: " + id));

        patientDrugRepository.delete(patientDrug);
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
            List<DrugInventory> activeInventories = drugInventoryRepository.findByDrugId(drug.getId()).stream()
                    .filter(DrugInventory::isActive)
                    .filter(inventory -> inventory.getCurrentQuantity() > 0)
                    .sorted((inv1, inv2) -> inv1.getExpiryDate().compareTo(inv2.getExpiryDate()))
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

        return patientDrugMapper.toDto(patientDrugRepository.save(patientDrug));
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
}
