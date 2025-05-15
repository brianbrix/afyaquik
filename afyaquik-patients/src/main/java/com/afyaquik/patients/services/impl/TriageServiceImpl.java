package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.TriageItemDto;
import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.dtos.patient.TriageReportItemDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.entity.TriageReportItem;
import com.afyaquik.patients.mappers.TriageReportItemMapper;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.repository.TriageItemRepository;
import com.afyaquik.patients.repository.TriageReportItemRepository;
import com.afyaquik.patients.repository.TriageReportRepository;
import com.afyaquik.patients.services.TriageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TriageServiceImpl implements TriageService {
    private final PatientVisitRepo patientVisitRepository;
    private final TriageReportRepository triageReportRepository;
    private final TriageReportItemRepository triageReportItemRepository;
    private final TriageItemRepository triageItemRepository;
    private final TriageReportItemMapper triageReportItemMapper;

    @Override
    public TriageReportDto updateTriageReport(Long visitId, List<TriageReportItemDto> triageReportItemDtos) {
        PatientVisit patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        TriageReport triageReport = new TriageReport();
        if (patientVisit.getTriageReport() != null) {
            triageReport = patientVisit.getTriageReport();
        }
        for (TriageReportItemDto triageReportItemDto : triageReportItemDtos) {
            TriageReportItem triageReportItem = new TriageReportItem();
            triageReportItem.setTriageItem(triageItemRepository.
                    findByName(triageReportItemDto.getName()).orElseThrow(() ->
                            new EntityNotFoundException("Triage item not found")));
            triageReportItem.setItemSummary(triageReportItemDto.getValue());
            triageReportItem.setTriageReport(triageReport);
            triageReport.getTriageReportItems().add(triageReportItem);
        }
        triageReport = triageReportRepository.save(triageReport);

        return TriageReportDto.builder()
                .id(triageReport.getId())
                .patientVisitId(patientVisit.getId())
                .triageReportItems(triageReport.getTriageReportItems().stream().map(triageReportItem ->
                        TriageReportItemDto.builder()
                                .name(triageReportItem.getTriageItem().getName())
                                .value(triageReportItem.getItemSummary())
                                .build()).toList())
                .build();
    }

    @Override
    public ListFetchDto<TriageReportItemDto> getTriageReportItemsForVisit(Long visitId, Pageable pageable) {
        PatientVisit patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        if (patientVisit.getTriageReport() != null) {
            ListFetchDto.<TriageReportItemDto>builder()
                    .results( triageReportItemRepository.findAllByTriageReport(pageable, patientVisit.getTriageReport()).map(
                            triageReportItemMapper::toDto
                    ))
                    .build();

        }
        return new ListFetchDto<>();
    }

}





