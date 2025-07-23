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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationServiceImplTest {

    @Mock
    private ObservationReportRepository observationReportRepository;

    @Mock
    private ObservationReportItemRepository observationReportItemRepository;

    @Mock
    private ObservationItemRepository observationItemRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private ObservationItemMapper observationItemMapper;

    @Mock
    private ObservationReportMapper observationReportMapper;

    @Mock
    private ObservationReportItemMapper observationReportItemMapper;

    @Mock
    private UserService userService;

    @Mock
    private PatientVisitService patientVisitService;

    @Mock
    private ObservationItemCategoryRepository observationItemCategoryRepository;

    @Mock
    private ObservationItemCategoryMapper observationItemCategoryMapper;

    @InjectMocks
    private ObservationServiceImpl observationService;

    private ObservationItemCategory category;
    private ObservationItemCategoryDto categoryDto;
    private ObservationItem item;
    private ObservationItemDto itemDto;
    private Station station;
    private User doctor;
    private PatientVisit patientVisit;
    private ObservationReport report;
    private ObservationReportDto reportDto;
    private ObservationReportItem reportItem;
    private ObservationReportItemDto reportItemDto;

    @BeforeEach
    void setUp() {
        // Set up test data
        category = ObservationItemCategory.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .build();

        categoryDto = ObservationItemCategoryDto.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .build();

        station = new Station();
        station.setId(1L);
        station.setName("Test Station");

        item = ObservationItem.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .mandatory(true)
                .category(category)
                .station(station)
                .price(100.0)
                .build();

        itemDto = ObservationItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .mandatory(true)
                .category(1L)
                .station(1L)
                .price(100.0)
                .build();

        doctor = new User();
        doctor.setId(1L);
        doctor.setFirstName("Test");
        doctor.setLastName("Doctor");

        patientVisit = new PatientVisit();
        patientVisit.setId(1L);

        report = ObservationReport.builder()
                .id(1L)
                .patientVisit(patientVisit)
                .doctor(doctor)
                .build();

        reportItem = ObservationReportItem.builder()
                .id(1L)
                .observationReport(report)
                .item(item)
                .value("Test Value")
                .comment("Test Comment")
                .build();

        List<ObservationReportItem> items = new ArrayList<>();
        items.add(reportItem);
        report.setObservationReportItems(items);

        reportItemDto = ObservationReportItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("Test Item")
                .value("Test Value")
                .comment("Test Comment")
                .build();

        List<ObservationReportItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(reportItemDto);

        reportDto = ObservationReportDto.builder()
                .id(1L)
                .patientVisitId(1L)
                .doctorId(1L)
                .observationReportItems(itemDtos)
                .build();
    }

    @Test
    void addObservationItemCategory() {
        // Given
        when(observationItemCategoryRepository.save(any(ObservationItemCategory.class))).thenReturn(category);
        when(observationItemCategoryMapper.toDto(any(ObservationItemCategory.class))).thenReturn(categoryDto);

        // When
        ObservationItemCategoryDto result = observationService.addObservationItemCategory(categoryDto);

        // Then
        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getName(), result.getName());
        assertEquals(categoryDto.getDescription(), result.getDescription());
        verify(observationItemCategoryRepository, times(1)).save(any(ObservationItemCategory.class));
        verify(observationItemCategoryMapper, times(1)).toDto(any(ObservationItemCategory.class));
    }

    @Test
    void updateObservationItemCategory() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(observationItemCategoryRepository.save(any(ObservationItemCategory.class))).thenReturn(category);
        when(observationItemCategoryMapper.toDto(any(ObservationItemCategory.class))).thenReturn(categoryDto);

        // When
        ObservationItemCategoryDto result = observationService.updateObservationItemCategory(1L, categoryDto);

        // Then
        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getName(), result.getName());
        assertEquals(categoryDto.getDescription(), result.getDescription());
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, times(1)).save(any(ObservationItemCategory.class));
        verify(observationItemCategoryMapper, times(1)).toDto(any(ObservationItemCategory.class));
    }

    @Test
    void updateObservationItemCategory_NotFound() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> observationService.updateObservationItemCategory(1L, categoryDto));
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, never()).save(any(ObservationItemCategory.class));
    }

    @Test
    void getObservationItemCategory() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(observationItemCategoryMapper.toDto(any(ObservationItemCategory.class))).thenReturn(categoryDto);

        // When
        ObservationItemCategoryDto result = observationService.getObservationItemCategory(1L);

        // Then
        assertNotNull(result);
        assertEquals(categoryDto.getId(), result.getId());
        assertEquals(categoryDto.getName(), result.getName());
        assertEquals(categoryDto.getDescription(), result.getDescription());
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryMapper, times(1)).toDto(any(ObservationItemCategory.class));
    }

    @Test
    void getObservationItemCategory_NotFound() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> observationService.getObservationItemCategory(1L));
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
    }

    @Test
    void getObservationItemCategories() {
        // Given
        Page<ObservationItemCategory> page = new PageImpl<>(List.of(category));
        when(observationItemCategoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(observationItemCategoryMapper.toDto(any(ObservationItemCategory.class))).thenReturn(categoryDto);

        // When
        ListFetchDto<ObservationItemCategoryDto> result = observationService.getObservationItemCategories(Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertNotNull(result.getResults());
        verify(observationItemCategoryRepository, times(1)).findAll(any(Pageable.class));
        verify(observationItemCategoryMapper, times(1)).toDto(any(ObservationItemCategory.class));
    }

    @Test
    void deleteObservationItemCategory() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // When
        observationService.deleteObservationItemCategory(1L);

        // Then
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, times(1)).delete(any(ObservationItemCategory.class));
    }

    @Test
    void deleteObservationItemCategory_NotFound() {
        // Given
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> observationService.deleteObservationItemCategory(1L));
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, never()).delete(any(ObservationItemCategory.class));
    }

    @Test
    void addObservationItem() {
        // Given
        when(observationItemRepository.findByNameAndCategoryId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(stationRepository.findById(anyLong())).thenReturn(Optional.of(station));
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(observationItemRepository.save(any(ObservationItem.class))).thenReturn(item);
        when(observationItemMapper.toDto(any(ObservationItem.class))).thenReturn(itemDto);

        // When
        ObservationItemDto result = observationService.addObservationItem(itemDto);

        // Then
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        verify(observationItemRepository, times(1)).findByNameAndCategoryId(anyString(), anyLong());
        verify(stationRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemRepository, times(1)).save(any(ObservationItem.class));
        verify(observationItemMapper, times(1)).toDto(any(ObservationItem.class));
    }

    @Test
    void addObservationItem_AlreadyExists() {
        // Given
        when(observationItemRepository.findByNameAndCategoryId(anyString(), anyLong())).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(EntityExistsException.class, () -> observationService.addObservationItem(itemDto));
        verify(observationItemRepository, times(1)).findByNameAndCategoryId(anyString(), anyLong());
        verify(stationRepository, never()).findById(anyLong());
        verify(observationItemCategoryRepository, never()).findById(anyLong());
        verify(observationItemRepository, never()).save(any(ObservationItem.class));
    }

    @Test
    void updateObservationItem() {
        // Given
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(stationRepository.findById(anyLong())).thenReturn(Optional.of(station));
        when(observationItemCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(observationItemRepository.save(any(ObservationItem.class))).thenReturn(item);
        when(observationItemMapper.toDto(any(ObservationItem.class))).thenReturn(itemDto);

        // When
        ObservationItemDto result = observationService.updateObservationItem(1L, itemDto);

        // Then
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        verify(observationItemRepository, times(1)).findById(anyLong());
        verify(stationRepository, times(1)).findById(anyLong());
        verify(observationItemCategoryRepository, times(1)).findById(anyLong());
        verify(observationItemRepository, times(1)).save(any(ObservationItem.class));
        verify(observationItemMapper, times(1)).toDto(any(ObservationItem.class));
    }

    @Test
    void updateObservationItem_NotFound() {
        // Given
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> observationService.updateObservationItem(1L, itemDto));
        verify(observationItemRepository, times(1)).findById(anyLong());
        verify(stationRepository, never()).findById(anyLong());
        verify(observationItemCategoryRepository, never()).findById(anyLong());
        verify(observationItemRepository, never()).save(any(ObservationItem.class));
    }

    @Test
    void getObservationItem() {
        // Given
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(observationItemMapper.toDto(any(ObservationItem.class))).thenReturn(itemDto);

        // When
        ObservationItemDto result = observationService.getObservationItem(1L);

        // Then
        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        verify(observationItemRepository, times(1)).findById(anyLong());
        verify(observationItemMapper, times(1)).toDto(any(ObservationItem.class));
    }

    @Test
    void getObservationItem_NotFound() {
        // Given
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityExistsException.class, () -> observationService.getObservationItem(1L));
        verify(observationItemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getObservationItems() {
        // Given
        Page<ObservationItem> page = new PageImpl<>(List.of(item));
        when(observationItemRepository.getAllPaginated(any(Pageable.class))).thenReturn(page);
        when(observationItemMapper.toDto(any(ObservationItem.class))).thenReturn(itemDto);

        // When
        ListFetchDto<ObservationItemDto> result = observationService.getObservationItems(Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertNotNull(result.getResults());
        verify(observationItemRepository, times(1)).getAllPaginated(any(Pageable.class));
        verify(observationItemMapper, times(1)).toDto(any(ObservationItem.class));
    }

    @Test
    void deleteObservationItem() {
        // Given
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // When
        observationService.deleteObservationItem(1L);

        // Then
        verify(observationItemRepository, times(1)).findById(anyLong());
        verify(observationItemRepository, times(1)).delete(any(ObservationItem.class));
    }

    @Test
    void addObservationReport() {
        // Given
        when(patientVisitService.getPatientVisit(anyLong())).thenReturn(patientVisit);
        when(userService.getById(anyLong())).thenReturn(doctor);
        when(observationItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(observationReportRepository.save(any(ObservationReport.class))).thenReturn(report);
        when(observationReportMapper.toDto(any(ObservationReport.class))).thenReturn(reportDto);

        // When
        ObservationReportDto result = observationService.addObservationReport(reportDto);

        // Then
        assertNotNull(result);
        assertEquals(reportDto.getId(), result.getId());
        verify(patientVisitService, times(1)).getPatientVisit(anyLong());
        verify(userService, times(1)).getById(anyLong());
        verify(observationItemRepository, times(1)).findById(anyLong());
        verify(observationReportRepository, times(1)).save(any(ObservationReport.class));
        verify(observationReportMapper, times(1)).toDto(any(ObservationReport.class));
    }

    @Test
    void getObservationReport() {
        // Given
        when(observationReportRepository.findById(anyLong())).thenReturn(Optional.of(report));
        when(observationReportMapper.toDto(any(ObservationReport.class))).thenReturn(reportDto);

        // When
        ObservationReportDto result = observationService.getObservationReport(1L);

        // Then
        assertNotNull(result);
        assertEquals(reportDto.getId(), result.getId());
        verify(observationReportRepository, times(1)).findById(anyLong());
        verify(observationReportMapper, times(1)).toDto(any(ObservationReport.class));
    }

    @Test
    void getObservationReport_NotFound() {
        // Given
        when(observationReportRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityExistsException.class, () -> observationService.getObservationReport(1L));
        verify(observationReportRepository, times(1)).findById(anyLong());
    }
}
