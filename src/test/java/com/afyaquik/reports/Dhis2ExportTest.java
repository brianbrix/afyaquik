package com.afyaquik.reports;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Dhis2ExportTest {
    private final Dhis2Settings settings = new Dhis2Settings();
    private final FacilityReportingService reports = mock(FacilityReportingService.class);
    private final Dhis2ExportService service = new Dhis2ExportService(settings, reports);

    @Test
    void unreviewedMetadataCannotProduceAnExport() {
        assertFalse(service.configuration().ready());
        assertThrows(IllegalArgumentException.class, () -> service.export("202001"));
        verifyNoInteractions(reports);
    }

    @Test
    void exportedValuesAreAggregateOnlyAndDoNotMarkDatasetComplete() {
        configured();
        when(reports.summarize(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)))
                .thenReturn(new FacilityReportingService.Summary(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31), 7, 8, 9, 10, Map.of(), Map.of(), 2, 1));
        var data = service.export("202001");
        assertEquals("202001", data.period());
        assertEquals("7", data.dataValues().get(0).value());
        assertEquals("Element0001", data.dataValues().get(0).dataElement());
        assertTrue(Arrays.stream(data.getClass().getRecordComponents()).noneMatch(component -> Set.of("patientId", "patientName", "completeDate").contains(component.getName())));
    }

    @Test
    void duplicateTargetsAndInvalidPeriodsAreRejected() {
        configured();
        assertThrows(IllegalArgumentException.class, () -> service.export("202013"));
        Dhis2Settings.Mapping duplicate = new Dhis2Settings.Mapping();
        duplicate.setMetric("encountersRecorded"); duplicate.setDataElement("Element0001"); duplicate.setCategoryOptionCombo("Category001");
        settings.getMappings().add(duplicate);
        assertFalse(service.configuration().ready());
        assertThrows(IllegalArgumentException.class, () -> service.export("202001"));
    }

    private void configured() {
        settings.setMappingApproved(true); settings.setMappingVersion("synthetic-v1"); settings.setDataSet("DataSet0001");
        settings.setOrgUnit("OrgUnit0001"); settings.setAttributeOptionCombo("Attribute01");
        Dhis2Settings.Mapping mapping = new Dhis2Settings.Mapping();
        mapping.setMetric("registrationsRecorded"); mapping.setDataElement("Element0001"); mapping.setCategoryOptionCombo("Category001");
        settings.getMappings().add(mapping);
    }
}