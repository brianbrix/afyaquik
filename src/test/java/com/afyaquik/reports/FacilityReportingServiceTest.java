package com.afyaquik.reports;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FacilityReportingServiceTest {
    private final EntityManager entityManager = mock(EntityManager.class);
    private final FacilityReportingService service = new FacilityReportingService(entityManager);

    @Test
    void invalidPeriodsDoNotQueryPatientData() {
        LocalDate today = LocalDate.of(2026, 10, 4);
        assertThrows(IllegalArgumentException.class, () -> service.summarize(today, today.minusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> service.summarize(today.minusDays(400), today));
        verifyNoInteractions(entityManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    void returnsAggregatedActivityForRequestedPeriod() {
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        TypedQuery<Object[]> statusQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(statusQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(3L);
        when(statusQuery.setParameter(anyString(), any())).thenReturn(statusQuery);
        when(statusQuery.getResultList()).thenReturn(List.<Object[]>of(new Object[] {"PENDING", 2L}));
        LocalDate from = LocalDate.of(2026, 10, 1);
        LocalDate to = LocalDate.of(2026, 10, 4);
        var summary = service.summarize(from, to);
        assertEquals(3L, summary.registeredPatients());
        assertEquals(2L, summary.visits());
        assertEquals(2L, summary.appointments());
        assertEquals(3L, summary.prescriptions());
        assertEquals(3L, summary.inpatientAdmissions());
        assertEquals(3L, summary.inpatientDischarges());
        verify(countQuery, times(4)).setParameter("until", to.plusDays(1).atStartOfDay());
    }
}