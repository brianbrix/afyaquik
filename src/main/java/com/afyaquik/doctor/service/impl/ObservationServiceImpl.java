package com.afyaquik.doctor.service.impl;

import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.dto.ObservationReportItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.doctor.entity.ObservationReport;
import com.afyaquik.doctor.entity.ObservationReportItem;
import com.afyaquik.doctor.enums.ObservationType;
import com.afyaquik.doctor.repository.ObservationItemRepository;
import com.afyaquik.doctor.repository.ObservationReportItemRepository;
import com.afyaquik.doctor.repository.ObservationReportRepository;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.services.PatientVisitService;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.service.UserService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.doctor.ObservationItemMapper;
import com.afyaquik.utils.mappers.doctor.ObservationReportItemMapper;
import com.afyaquik.utils.mappers.doctor.ObservationReportMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservationServiceImpl implements ObservationService {
    private final ObservationReportRepository  observationReportRepository;
    private final ObservationReportItemRepository observationReportItemRepository;
    private final ObservationItemRepository observationItemRepository;
    private final ObservationItemMapper observationItemMapper;
    private final ObservationReportMapper  observationReportMapper;
    private final ObservationReportItemMapper observationReportItemMapper;
    private final UserService  userService;
    private final PatientVisitService patientVisitService;
    @Override
    public ObservationItemDto addObservationItem(ObservationItemDto observationItemDto) {
        observationItemRepository.findByNameAndType(observationItemDto.getName(),ObservationType.valueOf(observationItemDto.getType())).ifPresent(item -> {
            throw new EntityExistsException("Observation item with name " + observationItemDto.getName()+ " and type "+observationItemDto.getType() + " already exists");
        });
        ObservationItem  observationItem = ObservationItem.builder()
                .name(observationItemDto.getName())
                .type(ObservationType.valueOf(observationItemDto.getType()))
                .price(observationItemDto.getPrice())
                .build();
       return observationItemMapper.toDto(observationItemRepository.save(observationItem));
    }

    @Override
    public ObservationItemDto updateObservationItem(Long id, ObservationItemDto observationItemDto) {
        ObservationItem   observationItem = observationItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Observation item not found"));
        observationItem.setName(observationItemDto.getName());
        observationItem.setType(ObservationType.valueOf(observationItemDto.getType()));
        observationItem.setPrice(observationItemDto.getPrice());
        return observationItemMapper.toDto(observationItemRepository.save(observationItem));
    }

    @Override
    public ObservationItemDto getObservationItem(Long id) {
        return observationItemMapper.toDto(observationItemRepository.findById(id).orElseThrow(() -> new EntityExistsException("Observation item not found")));
    }


    @Override
    public ListFetchDto<ObservationItemDto> getObservationItems(Pageable pageable) {
        return ListFetchDto.<ObservationItemDto>builder()
                .results(observationItemRepository.getAllPaginated(pageable).map(observationItemMapper::toDto))
                .build();
    }

    @Override
    public void deleteObservationItem(Long id) {
        observationItemRepository.findById(id).ifPresent(observationItemRepository::delete);
    }

    @Override
    public ObservationReportDto addObservationReport(ObservationReportDto observationReportDto) {
        PatientVisit  patientVisit = patientVisitService.getPatientVisit(observationReportDto.getPatientVisitId());
        User doctor = userService.getById(observationReportDto.getDoctorId());
        ObservationReport observationReport = ObservationReport.builder()
                .patientVisit(patientVisit)
                .doctor(doctor)
                .build();
        List<ObservationReportItem> items= new ArrayList<>();
        observationReportDto.getItems()
                .forEach(item -> {
            ObservationItem observationItem = observationItemRepository.findById(item.getItemId()).orElseThrow(() -> new EntityExistsException("Observation item not found"));
            ObservationReportItem observationReportItem = ObservationReportItem.builder()
                    .observationReport(observationReport)
                    .item(observationItem)
                    .value(item.getValue())
                    .comment(item.getComment())
                    .build();
            items.add(observationReportItem);
        });
        observationReport.setObservationReportItems(items);
        return observationReportMapper.toDto(observationReportRepository.save(observationReport));
    }

    @Override
    public ObservationReportDto updateObservationReport(Long id, ObservationReportDto observationReportDto) {
        return null;
    }

    @Override
    public ObservationReportDto getObservationReport(Long id) {
        return observationReportMapper.toDto(observationReportRepository.findById(id).orElseThrow(() -> new EntityExistsException("Observation report not found")));
    }

    @Override
    public void deleteObservationReport(Long id) {
        observationReportRepository.findById(id).ifPresent(observationReportRepository::delete);
    }

    @Override
    public ObservationReportItemDto getObservationReportItem(Long id) {
        return observationReportItemMapper.toDto(observationReportItemRepository.findById(id).orElseThrow(() -> new EntityExistsException("Observation report item not found")));
    }

    @Override
    public ListFetchDto<ObservationReportDto> getPatientReports(Long patientId, Pageable pageable) {
        return ListFetchDto.<ObservationReportDto>builder()
                .results(observationReportRepository.findByPatientId(patientId, pageable).map(observationReportMapper::toDto))
                .build();
    }
}
