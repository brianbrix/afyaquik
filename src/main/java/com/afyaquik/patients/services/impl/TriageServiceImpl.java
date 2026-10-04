package com.afyaquik.patients.services.impl;

import com.afyaquik.utils.exceptions.DuplicateValueException;
import com.afyaquik.patients.dto.TriageItemDto;
import com.afyaquik.patients.dto.TriageReportDto;
import com.afyaquik.patients.dto.TriageReportItemDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.entity.TriageItem;
import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.entity.TriageReportItem;
import com.afyaquik.utils.mappers.patients.TriageItemMapper;
import com.afyaquik.utils.mappers.patients.TriageReportItemMapper;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.repository.TriageItemRepository;
import com.afyaquik.patients.repository.TriageReportItemRepository;
import com.afyaquik.patients.repository.TriageReportRepository;
import com.afyaquik.patients.services.TriageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class TriageServiceImpl implements TriageService {
    private final PatientVisitRepo patientVisitRepository;
    private final TriageReportRepository triageReportRepository;
    private final TriageReportItemRepository triageReportItemRepository;
    private final TriageItemRepository triageItemRepository;
    private final TriageReportItemMapper triageReportItemMapper;
    private final TriageItemMapper triageItemMapper;

    @Override
    @Transactional
    public TriageReportDto updateTriageReport(Long visitId, List<TriageReportItemDto> triageReportItemDtos) {
        if (triageReportItemDtos == null || triageReportItemDtos.isEmpty()) {
            throw new IllegalArgumentException("At least one triage measurement is required");
        }
        var names = new HashSet<String>();
        for (TriageReportItemDto item : triageReportItemDtos) {
            if (item == null || item.getName() == null || item.getName().isBlank()
                    || item.getValue() == null || item.getValue().isBlank() || !names.add(item.getName())) {
                throw new IllegalArgumentException("Each triage measurement must have a unique name and a value");
            }
        }
        PatientVisit patientVisit = patientVisitRepository.findForUpdate(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        if (patientVisit.isDeleted() || patientVisit.getPatient().isDeleted()
            || patientVisit.getVisitStatus() == com.afyaquik.patients.enums.Status.COMPLETED || patientVisit.getVisitStatus() == com.afyaquik.patients.enums.Status.CANCELLED) {
            throw new IllegalArgumentException("Closed or archived encounters cannot receive new triage measurements");
        }
        TriageReport triageReport = new TriageReport();
        if (patientVisit.getTriageReport() != null) {
            triageReport = patientVisit.getTriageReport();
        }
        for (TriageReportItemDto triageReportItemDto : triageReportItemDtos) {
            TriageItem triageItem = triageItemRepository.findByName(triageReportItemDto.getName())
                .orElseThrow(() -> new EntityNotFoundException("Triage item not found"));
            TriageReportItem triageReportItem = triageReport.getTriageReportItems().stream()
                .filter(item -> triageReportItemDto.getName().equals(item.getTriageItem().getName()))
                .findFirst().orElse(null);
            if (triageReportItem == null) {
            triageReportItem = new TriageReportItem();
            triageReportItem.setTriageItem(triageItem);
            triageReport.getTriageReportItems().add(triageReportItem);
            }
            triageReportItem.setItemSummary(triageReportItemDto.getValue().trim());
            triageReportItem.setTriageReport(triageReport);
        }
        triageReport.setPatientVisit(patientVisit);
        triageReport = triageReportRepository.save(triageReport);
        patientVisit.setTriageReport(triageReport);
        patientVisitRepository.save(patientVisit);

        return TriageReportDto.builder()
                .id(triageReport.getId())
                .patientVisitId(patientVisit.getId())
                .triageReportItems(triageReport.getTriageReportItems().stream().map(triageReportItemMapper::toDto).toList())
                .build();
    }

    @Override
    public ListFetchDto<TriageReportItemDto> getTriageReportItemsForVisit(Long visitId, Pageable pageable) {
        PatientVisit patientVisit = patientVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
        if (patientVisit.getTriageReport() != null) {
            return ListFetchDto.<TriageReportItemDto>builder()
                    .results( triageReportItemRepository.findAllByTriageReport(pageable, patientVisit.getTriageReport()).map(
                            triageReportItemMapper::toDto
                    ))
                    .build();

        }
        return ListFetchDto.<TriageReportItemDto>builder().results(Page.empty(pageable)).build();
    }

    @Override
    public List<TriageItemDto> getTriageItems() {
        return triageItemRepository.findAll().stream().map(triageItemMapper::toDto).toList();
    }

    @Override
    public TriageItemDto createTriageItem(TriageItemDto triageItemDto) {
        triageItemRepository.findByName(triageItemDto.getName()).ifPresent(x->{
            throw new DuplicateValueException("Triage item already exists");
        });
        TriageItem triageItem = triageItemMapper.toEntity(triageItemDto);
        triageItem = triageItemRepository.save(triageItem);
        return triageItemMapper.toDto(triageItem);
    }

    @Override
    public TriageItemDto updateTriageItem(Long id, TriageItemDto triageItemDto) {
        TriageItem triageItem = triageItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Triage item not found"));
        triageItem.setName(triageItemDto.getName());
        triageItem.setDescription(triageItemDto.getDescription());
        triageItem = triageItemRepository.save(triageItem);
        return triageItemMapper.toDto(triageItem);
    }

    @Override
    public void deleteTriageItem(Long id) {
        triageItemRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Triage item not found"));
        triageItemRepository.deleteById(id);
    }

    @Override
    public TriageItemDto getTriageItem(Long itemId) {
        return triageItemMapper.toDto(triageItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Triage item not found")));

    }

}





