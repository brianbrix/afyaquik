package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapperRegistry {

    @Autowired
    DoctorMapperRegistry(ObservationItemMapper observationItemMapper, ObservationItemCategoryMapper observationItemCategoryMapper,
                         TreatmentPlanMapper treatmentPlanMapper, TreatmentPlanItemMapper treatmentPlanItemMapper, TreatmentPlanReportItemMapper treatmentPlanReportItemMapper,
                         MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("observationItems", observationItemMapper);
            mapperRegistry.registerMapper("observationItemCategories", observationItemCategoryMapper);
            mapperRegistry.registerMapper("treatmentPlans", treatmentPlanMapper);
            mapperRegistry.registerMapper("treatmentPlanItems", treatmentPlanItemMapper);
            mapperRegistry.registerMapper("treatmentPlanReportItems", treatmentPlanReportItemMapper);
        }
    }
}
