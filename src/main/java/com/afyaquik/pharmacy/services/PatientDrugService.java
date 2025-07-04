package com.afyaquik.pharmacy.services;

import com.afyaquik.pharmacy.dto.PatientDrugDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientDrugService {

    PatientDrugDto assignDrugToPatientVisit(PatientDrugDto patientDrugDto);
    List<PatientDrugDto> assignManyDrugsToPatientVisit(List<PatientDrugDto> patientDrugDtos);

    PatientDrugDto getPatientDrugById(Long id);

    List<PatientDrugDto> getDrugsForPatientVisit(Long patientVisitId);

    ListFetchDto<PatientDrugDto> getDrugsForPatientVisit(Long patientVisitId, Pageable pageable);

    PatientDrugDto updatePatientDrug(Long id, PatientDrugDto patientDrugDto);

    void deletePatientDrug(Long id);

    PatientDrugDto dispenseDrug(Long id);

    List<PatientDrugDto> getDispensedDrugsForPatientVisit(Long patientVisitId);

    List<PatientDrugDto> getUndispensedDrugsForPatientVisit(Long patientVisitId);
}
