package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.DrugFormDto;

public interface DrugFormService {
    DrugFormDto createDrugForm(DrugFormDto drugFormDto);
    DrugFormDto getDrugFormById(Long id);
    DrugFormDto updateDrugForm(Long id, DrugFormDto drugFormDto);
    void deleteDrugForm(Long id);
}
