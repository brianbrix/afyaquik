package com.afyaquik.patients.services;

import com.afyaquik.dtos.patient.TriageItemDto;
import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.dtos.patient.TriageReportItemDto;
import com.afyaquik.dtos.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TriageService {
    TriageReportDto updateTriageReport(Long visitId, List<TriageReportItemDto> triageReportItemDtos);
    ListFetchDto<TriageReportItemDto> getTriageReportItemsForVisit(Long visitId, Pageable pageable);
    List<TriageItemDto> getTriageItems();
    TriageItemDto createTriageItem(TriageItemDto triageItemDto);
    TriageItemDto updateTriageItem(Long id, TriageItemDto triageItemDto);
    void deleteTriageItem(Long id);
    TriageItemDto getTriageItem(Long visitId);
}
