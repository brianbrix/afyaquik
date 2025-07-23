package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.DrugDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface DrugService {
    DrugDto createDrug(DrugDto drugDto);
    DrugDto getDrugById(Long id);
    ListFetchDto<DrugDto> getAllDrugs(Pageable pageable);
    ListFetchDto<DrugDto> getDrugsByEnabled(Pageable pageable, boolean enabled);
    DrugDto updateDrug(Long id, DrugDto drugDto);
    void deleteDrug(Long id);
}
