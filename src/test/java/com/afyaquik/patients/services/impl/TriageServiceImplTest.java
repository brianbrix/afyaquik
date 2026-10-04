package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.TriageReportItemDto;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.entity.TriageItem;
import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.entity.TriageReportItem;
import com.afyaquik.patients.repository.*;
import com.afyaquik.utils.mappers.patients.TriageItemMapper;
import com.afyaquik.utils.mappers.patients.TriageReportItemMapper;
import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TriageServiceImplTest {
    private final PatientVisitRepo visits = mock(PatientVisitRepo.class);
    private final TriageReportRepository reports = mock(TriageReportRepository.class);
    private final TriageReportItemRepository reportItems = mock(TriageReportItemRepository.class);
    private final TriageItemRepository items = mock(TriageItemRepository.class);
    private final TriageServiceImpl service = new TriageServiceImpl(visits, reports, reportItems, items,
            mock(TriageReportItemMapper.class), mock(TriageItemMapper.class));
    private PatientVisit visit;

    @BeforeEach
    void setUp() {
        visit = PatientVisit.builder().id(1L).patient(com.afyaquik.patients.entity.Patient.builder().build()).build();
        when(visits.findById(1L)).thenReturn(Optional.of(visit));
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(visit));
        when(reports.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        TriageItem weight = new TriageItem();
        weight.setName("Weight (kg)");
        when(items.findByName(weight.getName())).thenReturn(Optional.of(weight));
    }

    @Test
    void saveAttachesReportToOwningVisit() {
        service.updateTriageReport(1L, List.of(measurement("65")));
        assertNotNull(visit.getTriageReport());
        assertSame(visit, visit.getTriageReport().getPatientVisit());
        assertEquals("65", visit.getTriageReport().getTriageReportItems().get(0).getItemSummary());
        verify(visits).save(visit);
    }

    @Test
    void updatingMeasurementDoesNotCreateDuplicates() {
        service.updateTriageReport(1L, List.of(measurement("65")));
        service.updateTriageReport(1L, List.of(measurement("66")));
        assertEquals(1, visit.getTriageReport().getTriageReportItems().size());
        assertEquals("66", visit.getTriageReport().getTriageReportItems().get(0).getItemSummary());
    }

    @Test
    void readReturnsConstructedPageInsteadOfDiscardingIt() {
        visit.setTriageReport(new TriageReport());
        var pageable = PageRequest.of(0, 20);
        when(reportItems.findAllByTriageReport(pageable, visit.getTriageReport())).thenReturn(Page.empty(pageable));
        assertNotNull(service.getTriageReportItemsForVisit(1L, pageable).getResults());
    }

    @Test
    void duplicateMeasurementsAreRejectedBeforeWriting() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateTriageReport(1L, List.of(measurement("65"), measurement("66"))));
        verify(reports, never()).save(any());
    }

    @Test
    void reportsAndItemsUseDistinctTables() {
        assertNotEquals(TriageReport.class.getAnnotation(Table.class).name(),
                TriageReportItem.class.getAnnotation(Table.class).name());
    }

    private TriageReportItemDto measurement(String value) {
        return TriageReportItemDto.builder().name("Weight (kg)").value(value).build();
    }

    @Test
    void closedEncountersCannotReceiveNewMeasurements() {
        visit.setVisitStatus(com.afyaquik.patients.enums.Status.COMPLETED);
        assertThrows(IllegalArgumentException.class, () -> service.updateTriageReport(1L, List.of(measurement("65"))));
        verify(reports, never()).save(any());
    }
}