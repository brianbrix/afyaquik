package com.afyaquik.pharmacy.services.impl;

import com.afyaquik.doctor.dto.ObservationItemCategoryDto;
import com.afyaquik.pharmacy.dto.DrugCategoryDto;
import com.afyaquik.pharmacy.entity.DrugCategory;
import com.afyaquik.pharmacy.repository.DrugCategoryRepository;
import com.afyaquik.pharmacy.services.DrugCategoryService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.pharmacy.DrugCategoryMapper;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugCategoryServiceImpl implements DrugCategoryService {
    private final DrugCategoryRepository drugCategoryRepository;
    private final DrugCategoryMapper drugCategoryMapper;
    @Override
    public DrugCategoryDto createDrugCategory(DrugCategoryDto drugCategoryDto) {
        if (drugCategoryRepository.findByNameIgnoreCase(drugCategoryDto.getName()).isPresent()) {
            throw new EntityExistsException("Drug category with name " + drugCategoryDto.getName() + " already exists.");
        }
        DrugCategory drugCategory = drugCategoryMapper.toEntity(drugCategoryDto);
        return drugCategoryMapper.toDto(drugCategoryRepository.save(drugCategory));
    }

    @Override
    public DrugCategoryDto getDrugCategoryById(Long id) {
        return drugCategoryRepository.findById(id)
                .map(drugCategoryMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Drug category not found with id: " + id));
    }

    @Override
    public ListFetchDto<DrugCategoryDto> getAllDrugCategories(Pageable pageable) {
        return ListFetchDto.<DrugCategoryDto>builder()
                .results(drugCategoryRepository.findAll(pageable).map(drugCategoryMapper::toDto))
                .build();
    }

    @Override
    public DrugCategoryDto updateDrugCategory(Long id, DrugCategoryDto drugCategoryDto) {
        DrugCategory existingCategory = drugCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug category not found with id: " + id));
        existingCategory.setName(drugCategoryDto.getName());
        existingCategory.setDescription(drugCategoryDto.getDescription());
        existingCategory.setEnabled(drugCategoryDto.isEnabled());
        return drugCategoryMapper.toDto(drugCategoryRepository.save(existingCategory));
    }

    @Override
    public void deleteDrugCategory(Long id) {
        DrugCategory drugCategory = drugCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug category not found with id: " + id));
        drugCategoryRepository.delete(drugCategory);
    }
}
