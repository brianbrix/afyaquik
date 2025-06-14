package com.afyaquik.doctor.service.impl;

import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.dto.TreatmentPlanItemDto;
import com.afyaquik.doctor.entity.TreatmentPlan;
import com.afyaquik.doctor.entity.TreatmentPlanItem;
import com.afyaquik.doctor.repository.TreatmentPlanItemRepository;
import com.afyaquik.doctor.repository.TreatmentPlanRepository;
import com.afyaquik.doctor.service.TreatmentPlanService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.utils.mappers.doctor.TreatmentPlanItemMapper;
import com.afyaquik.utils.mappers.doctor.TreatmentPlanMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentPlanServiceImpl implements TreatmentPlanService {
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentPlanItemRepository treatmentPlanItemRepository;
    private final PatientVisitRepo patientVisitRepo;
    private final TreatmentPlanMapper treatmentPlanMapper;
    private final TreatmentPlanItemMapper treatmentPlanItemMapper;
    @Override
    public TreatmentPlanDto addTreatmentPlan(TreatmentPlanDto treatmentPlanDto) {
        List<TreatmentPlanItem> treatmentPlanItems = treatmentPlanItemRepository.findAllById(treatmentPlanDto.getTreatmentPlanItems().stream().map(TreatmentPlanItemDto::getId).toList());
        if (treatmentPlanItems.isEmpty())
            throw new EntityNotFoundException("No treatment plan items found with the provided IDs");
        PatientVisit patientVisit = patientVisitRepo.findById(treatmentPlanDto.getPatientVisitId()).orElseThrow(()-> new EntityNotFoundException("Patient visit not found"));
        return treatmentPlanMapper.toDto(treatmentPlanRepository.save(TreatmentPlan.builder()
                .description(treatmentPlanDto.getDescription())
                .patientVisit(patientVisit)
                .treatmentPlanItems(treatmentPlanItems)
                .build()));
    }


    @Override
    public TreatmentPlanDto getTreatmentPlan(Long id) {
        return treatmentPlanMapper.toDto(treatmentPlanRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Treatment plan not found")));
    }

    @Override
    public void deleteTreatmentPlan(Long id) {
        treatmentPlanRepository.findById(id).ifPresent(treatmentPlanRepository::delete);
    }

    @Override
    public TreatmentPlanItemDto addTreatmentPlanItem(TreatmentPlanItemDto treatmentPlanItemDto) {
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.save(TreatmentPlanItem.builder()
                .name(treatmentPlanItemDto.getName())
                .description(treatmentPlanItemDto.getDescription())
                .enabled(treatmentPlanItemDto.isEnabled())
                .build()));
    }

    @Override
    public TreatmentPlanItemDto updateTreatmentPlanItem(Long id, TreatmentPlanItemDto treatmentPlanItemDto) {
        TreatmentPlanItem treatmentPlanItem = treatmentPlanItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Treatment plan item not found"));
        treatmentPlanItem.setName(treatmentPlanItemDto.getName());
        treatmentPlanItem.setDescription(treatmentPlanItemDto.getDescription());
        treatmentPlanItem.setEnabled(treatmentPlanItemDto.isEnabled());
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.save(treatmentPlanItem));
    }

    @Override
    public TreatmentPlanItemDto getTreatmentPlanItem(Long id) {
        return treatmentPlanItemMapper.toDto(treatmentPlanItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Treatment plan item not found")));
    }

    @Override
    public void deleteTreatmentPlanItem(Long id) {
        treatmentPlanItemRepository.findById(id).ifPresent(treatmentPlanItemRepository::delete);
    }
}
