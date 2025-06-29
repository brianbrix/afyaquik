package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapperRegistry {

    @Autowired
    PatientMapperRegistry(PatientMapper patientMapper, PatientVisitMapper patientVisitMapper, TriageReportItemMapper triageReportItemMapper, TriageItemMapper triageItemMapper,PatientAssignmentsMapper patientAssignmentsMapper, MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("patients", patientMapper);
            mapperRegistry.registerMapper("visits", patientVisitMapper);
            mapperRegistry.registerMapper("triageReportItem", triageReportItemMapper);
            mapperRegistry.registerMapper("triageItems", triageItemMapper);
            mapperRegistry.registerMapper("assignments", patientAssignmentsMapper);

        }
    }
}
