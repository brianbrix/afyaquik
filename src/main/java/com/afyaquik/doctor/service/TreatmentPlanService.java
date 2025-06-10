package com.afyaquik.doctor.service;

import com.afyaquik.doctor.dto.TreatmentPlanDto;
import com.afyaquik.doctor.dto.TreatmentPlanItemDto;


public interface TreatmentPlanService {
    TreatmentPlanDto addTreatmentPlan(TreatmentPlanDto treatmentPlanDto);
    TreatmentPlanDto getTreatmentPlan(Long id);
    void deleteTreatmentPlan(Long id);

    TreatmentPlanItemDto addTreatmentPlanItem(TreatmentPlanItemDto treatmentPlanItemDto);
    TreatmentPlanItemDto updateTreatmentPlanItem(Long id, TreatmentPlanItemDto treatmentPlanItemDto);
    TreatmentPlanItemDto getTreatmentPlanItem(Long id);
    void deleteTreatmentPlanItem(Long id);
}
