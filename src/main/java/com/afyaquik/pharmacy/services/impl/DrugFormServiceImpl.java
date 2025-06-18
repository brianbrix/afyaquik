package com.afyaquik.pharmacy.services.impl;

import com.afyaquik.pharmacy.dto.DrugFormDto;
import com.afyaquik.pharmacy.repository.DrugFormRepository;
import com.afyaquik.pharmacy.services.DrugFormService;
import com.afyaquik.utils.mappers.pharmacy.DrugFormMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugFormServiceImpl implements DrugFormService {
    private final DrugFormMapper drugFormMapper;
    private final DrugFormRepository drugFormRepository;
    @Override
    public DrugFormDto createDrugForm(DrugFormDto drugFormDto) {
        drugFormRepository.findByNameIgnoreCase(drugFormDto.getName())
                .ifPresent(existingForm -> {
                    throw new EntityExistsException("Drug form with name " + drugFormDto.getName() + " already exists.");
                });
        return drugFormMapper.toDto(drugFormRepository.save(drugFormMapper.toEntity(drugFormDto)));
    }

    @Override
    public DrugFormDto getDrugFormById(Long id) {
        return drugFormRepository.findById(id)
                .map(drugFormMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Drug form not found with id: " + id));
    }

    @Override
    public DrugFormDto updateDrugForm(Long id, DrugFormDto drugFormDto) {
        return drugFormRepository.findById(id)
                .map(existingForm -> {
                    existingForm.setName(drugFormDto.getName());
                    existingForm.setDescription(drugFormDto.getDescription());
                    return drugFormMapper.toDto(drugFormRepository.save(existingForm));
                })
                .orElseThrow(() -> new EntityNotFoundException("Drug form not found with id: " + id));
    }

    @Override
    public void deleteDrugForm(Long id) {
        drugFormRepository.findById(id)
                .ifPresentOrElse(
                        drugFormRepository::delete,
                        () -> {
                            throw new EntityNotFoundException("Drug form not found with id: " + id);
                        }
                );
    }
}
