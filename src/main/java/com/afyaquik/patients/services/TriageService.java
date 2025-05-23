package com.afyaquik.patients.services;

import com.afyaquik.patients.dto.TriageItemDto;
import com.afyaquik.patients.dto.TriageReportDto;
import com.afyaquik.patients.dto.TriageReportItemDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
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
