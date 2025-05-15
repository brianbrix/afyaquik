package com.afyaquik.patients.mappers;

import com.afyaquik.core.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapperRegistry {

    @Autowired
    PatientMapperRegistry(PatientMapper patientMapper, PatientVisitMapper patientVisitMapper, TriageReportItemMapper triageReportItemMapper, MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("patients", patientMapper);
            mapperRegistry.registerMapper("visits", patientVisitMapper);
            mapperRegistry.registerMapper("triageReportItem", triageReportItemMapper);
        }
    }
}
