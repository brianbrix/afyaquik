package com.afyaquik.pharmacy.services.impl;

import com.afyaquik.pharmacy.dto.DrugDto;
import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.pharmacy.entity.DrugCategory;
import com.afyaquik.pharmacy.entity.DrugForm;
import com.afyaquik.pharmacy.entity.DrugInventory;
import com.afyaquik.pharmacy.repository.DrugCategoryRepository;
import com.afyaquik.pharmacy.repository.DrugFormRepository;
import com.afyaquik.pharmacy.repository.DrugInventoryRepository;
import com.afyaquik.pharmacy.repository.DrugRepository;
import com.afyaquik.pharmacy.services.DrugService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.pharmacy.DrugMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {
    private final DrugRepository drugRepository;
    private final DrugCategoryRepository drugCategoryRepository;
    private final DrugFormRepository drugFormRepository;
    private final DrugInventoryRepository drugInventoryRepository;
    private final DrugMapper drugMapper;

    @Override
    public DrugDto createDrug(DrugDto drugDto) {
        if (drugRepository.findByName(drugDto.getName()).isPresent()) {
            throw new EntityNotFoundException("Drug with name " + drugDto.getName() + " already exists.");
        }
        if (drugDto.getAtcCode()!=null && drugRepository.findByAtcCode(drugDto.getAtcCode()).isPresent()) {
            throw new EntityNotFoundException("Drug with ATC code " + drugDto.getAtcCode() + " already exists.");
        }
        DrugCategory drugCategory = drugCategoryRepository.findById(drugDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Drug category not found with id: " + drugDto.getCategoryId()));
        DrugForm drugForm = drugFormRepository.findById(drugDto.getDrugFormId())
                .orElseThrow(() -> new RuntimeException("Drug form not found with id: " + drugDto.getDrugFormId()));
        Drug drug = Drug.builder()
                .drugForm(drugForm)
                .name(drugDto.getName())
                .atcCode(drugDto.getAtcCode())
                .description(drugDto.getDescription())
                .brandName(drugDto.getBrandName())
                .enabled(drugDto.getEnabled())
                .manufacturer(drugDto.getManufacturer())
                .sampleDosageInstruction(drugDto.getSampleDosageInstruction())
                .strength(drugDto.getStrength())
                .isPrescriptionRequired(drugDto.getIsPrescriptionRequired())
                .drugCategory(drugDto.getCategoryId() != null ? drugCategory : null)
                .build();
        DrugDto dto = drugMapper.toDto(drugRepository.save(drug));
        //update to get correct stock quantity
        updateDrug(dto.getId(), dto);
        return dto;
    }

    @Override
    public DrugDto getDrugById(Long id) {
        return drugRepository.findById(id)
                .map(drugMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + id));
    }

    @Override
    public ListFetchDto<DrugDto> getAllDrugs(Pageable pageable) {
        return ListFetchDto.<DrugDto>builder()
                .results(drugRepository.findAll(pageable).map(drugMapper::toDto))
                .build();
    }

    @Override
    public DrugDto updateDrug(Long id, DrugDto drugDto) {
        Drug existingDrug = drugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with id: " + id));
        if (drugRepository.findByName(drugDto.getName()).isPresent()) {
            throw new EntityNotFoundException("Drug with name " + drugDto.getName() + " already exists.");
        }
        if (drugRepository.findByNameAndBrandName(drugDto.getName(), drugDto.getBrandName())
                .filter(drug -> !drug.getId().equals(id)).isPresent()) {
            throw new EntityNotFoundException("Drug with name " + drugDto.getName() + " already exists.");
        }

        if (drugRepository.findByAtcCode(drugDto.getAtcCode())
                .filter(drug -> !drug.getId().equals(id)).isPresent()) {
            throw new RuntimeException("Drug with ATC code " + drugDto.getAtcCode() + " already exists.");
        }
        int totalStock = drugInventoryRepository.findByDrugId(id)
                .stream()
                .mapToInt(DrugInventory::getCurrentQuantity)
                .sum();
        DrugForm drugForm = drugFormRepository.findById(drugDto.getDrugFormId())
                .orElseThrow(() -> new RuntimeException("Drug form not found with id: " + drugDto.getDrugFormId()));

        DrugCategory drugCategory = drugCategoryRepository.findById(drugDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Drug category not found with id: " + drugDto.getCategoryId()));
        existingDrug.setName(drugDto.getName());
        existingDrug.setBrandName(drugDto.getBrandName());
        existingDrug.setDescription(drugDto.getDescription());
        existingDrug.setDrugForm(drugForm);
        existingDrug.setStrength(drugDto.getStrength());
        existingDrug.setManufacturer(drugDto.getManufacturer());
        existingDrug.setSampleDosageInstruction(drugDto.getSampleDosageInstruction());
        existingDrug.setStockQuantity(totalStock);
        existingDrug.setPrescriptionRequired(drugDto.getIsPrescriptionRequired());
        existingDrug.setAtcCode(drugDto.getAtcCode());
        existingDrug.setEnabled(drugDto.getEnabled());
        existingDrug.setDrugCategory(drugDto.getCategoryId() != null ? drugCategory : null);
        return drugMapper.toDto(drugRepository.save(existingDrug));
    }

    @Override
    public void deleteDrug(Long id) {
        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug not found with id: " + id));
        drugRepository.delete(drug);
    }
}
