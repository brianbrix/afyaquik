package com.afyaquik.doctor.service.impl;

import com.afyaquik.doctor.dto.ObservationItemCategoryDto;
import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.dto.ObservationReportDto;
import com.afyaquik.doctor.dto.ObservationReportItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.doctor.entity.ObservationItemCategory;
import com.afyaquik.doctor.entity.ObservationReport;
import com.afyaquik.doctor.entity.ObservationReportItem;
import com.afyaquik.doctor.repository.ObservationItemCategoryRepository;
import com.afyaquik.doctor.repository.ObservationItemRepository;
import com.afyaquik.doctor.repository.ObservationReportItemRepository;
import com.afyaquik.doctor.repository.ObservationReportRepository;
import com.afyaquik.doctor.service.ObservationService;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.services.PatientVisitService;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.service.UserService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.utils.mappers.doctor.ObservationItemCategoryMapper;
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
    private final StationRepository stationRepository;
    private final ObservationItemMapper observationItemMapper;
    private final ObservationReportMapper  observationReportMapper;
    private final ObservationReportItemMapper observationReportItemMapper;
    private final UserService  userService;
    private final PatientVisitService patientVisitService;
    private final ObservationItemCategoryRepository observationItemCategoryRepository;
    private final ObservationItemCategoryMapper observationItemCategoryMapper;

    @Override
    public ObservationItemCategoryDto addObservationItemCategory(ObservationItemCategoryDto observationItemCategoryDto) {
        ObservationItemCategory observationItemCategory = ObservationItemCategory.builder()
                .name(observationItemCategoryDto.getName())
                .description(observationItemCategoryDto.getDescription())
                .build();
        return observationItemCategoryMapper.toDto(observationItemCategoryRepository.save(observationItemCategory));
    }

    @Override
    public ObservationItemCategoryDto updateObservationItemCategory(Long id, ObservationItemCategoryDto observationItemCategoryDto) {
        ObservationItemCategory observationItemCategory = observationItemCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Observation item category not found"));
        observationItemCategory.setName(observationItemCategoryDto.getName());
        observationItemCategory.setDescription(observationItemCategoryDto.getDescription());
        return observationItemCategoryMapper.toDto(observationItemCategoryRepository.save(observationItemCategory));
    }

    @Override
    public ObservationItemCategoryDto getObservationItemCategory(Long id) {
        return observationItemCategoryMapper.toDto(observationItemCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Observation item category not found")));
    }

    @Override
    public ListFetchDto<ObservationItemCategoryDto> getObservationItemCategories(Pageable pageable) {
        return ListFetchDto.<ObservationItemCategoryDto>builder()
                .results(observationItemCategoryRepository.findAll(pageable).map(observationItemCategoryMapper::toDto))
                .build();
    }

    @Override
    public void deleteObservationItemCategory(Long id) {
        ObservationItemCategory observationItemCategory = observationItemCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Observation item category not found"));
        observationItemCategoryRepository.delete(observationItemCategory);
    }

    @Override
    public ObservationItemDto addObservationItem(ObservationItemDto observationItemDto) {
        observationItemRepository.findByNameAndCategoryId(observationItemDto.getName(),observationItemDto.getCategory()).ifPresent(item -> {
            throw new EntityExistsException("Observation item with name " + observationItemDto.getName()+ " and category "+item.getCategory().getName() + " already exists");
        });
        Station station = stationRepository.findById(observationItemDto.getStation()).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        ObservationItemCategory observationItemCategory = observationItemCategoryRepository.findById(observationItemDto.getCategory()).orElseThrow(() -> new EntityNotFoundException("Observation item category not found"));
        ObservationItem  observationItem = ObservationItem.builder()
                .name(observationItemDto.getName())
                .description(observationItemDto.getDescription())
                .mandatory(observationItemDto.isMandatory())
                .category(observationItemCategory)
                .station(station)
                .price(observationItemDto.getPrice())

                .build();
       return observationItemMapper.toDto(observationItemRepository.save(observationItem));
    }

    @Override
    public ObservationItemDto updateObservationItem(Long id, ObservationItemDto observationItemDto) {
        ObservationItem   observationItem = observationItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Observation item not found"));
        Station station = stationRepository.findById(observationItemDto.getStation()).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        ObservationItemCategory observationItemCategory = observationItemCategoryRepository.findById(observationItemDto.getCategory()).orElseThrow(() -> new EntityNotFoundException("Observation item category not found"));
        observationItem.setName(observationItemDto.getName());
        observationItem.setDescription(observationItemDto.getDescription());
        observationItem.setStation(station);
        observationItem.setCategory(observationItemCategory);
        observationItem.setMandatory(observationItemDto.isMandatory());
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

    @Override
    public ListFetchDto<ObservationReportDto> getPatientVisitReports(Long visitId, Pageable pageable) {
        return ListFetchDto.<ObservationReportDto>builder()
                .results(observationReportRepository.findByPatientVisitId(visitId, pageable).map(observationReportMapper::toDto))
                .build();
    }
}
