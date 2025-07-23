package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.DrugCategoryDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface DrugCategoryService {
    DrugCategoryDto createDrugCategory(DrugCategoryDto drugCategoryDto);
    DrugCategoryDto getDrugCategoryById(Long id);
    ListFetchDto<DrugCategoryDto> getAllDrugCategories(Pageable pageable);
    DrugCategoryDto updateDrugCategory(Long id, DrugCategoryDto drugCategoryDto);
    void deleteDrugCategory(Long id);
}
