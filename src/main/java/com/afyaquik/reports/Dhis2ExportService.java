package com.afyaquik.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class Dhis2ExportService {
    private final Dhis2Settings settings;
    private final FacilityReportingService reporting;
    private static final Map<String, String> METRICS = Map.of(
            "registrationsRecorded", "Non-archived patient records created in the month",
            "encountersRecorded", "Non-archived encounters dated in the month, including cancelled encounters",
            "appointmentsRecorded", "Non-archived appointments scheduled in the month, including cancelled appointments",
            "prescriptionsRecorded", "Non-archived prescription records created in the month",
            "admissionsRecorded", "Non-archived inpatient admissions started in the month",
            "dischargesRecorded", "Non-archived inpatient discharges recorded in the month");

    public Configuration configuration() {
        List<String> errors = new ArrayList<>();
        if (!settings.isMappingApproved()) errors.add("DHIS2 mapping has not been approved for this facility");
        if (settings.getMappingVersion() == null || settings.getMappingVersion().isBlank() || settings.getMappingVersion().length() > 80) errors.add("A mapping version is required");
        if (!uid(settings.getDataSet())) errors.add("A verified dataset UID is required");
        if (!uid(settings.getOrgUnit())) errors.add("A verified facility organisation-unit UID is required");
        if (!uid(settings.getAttributeOptionCombo())) errors.add("A verified attribute-option-combination UID is required");
        Set<String> targets = new HashSet<>();
        Set<String> sources = new HashSet<>();
        if (settings.getMappings() == null || settings.getMappings().isEmpty()) errors.add("At least one reviewed aggregate mapping is required");
        else for (Dhis2Settings.Mapping mapping : settings.getMappings()) {
            if (mapping == null || mapping.getMetric() == null || !METRICS.containsKey(mapping.getMetric())) { errors.add("Mapping contains an unsupported aggregate source"); continue; }
            if (!uid(mapping.getDataElement()) || !uid(mapping.getCategoryOptionCombo())) errors.add("Each mapping requires verified data-element and category-option-combination UIDs");
            if (!sources.add(mapping.getMetric()) || !targets.add(mapping.getDataElement() + ":" + mapping.getCategoryOptionCombo())) errors.add("Duplicate mapping sources or targets are not allowed");
        }
        return new Configuration(errors.isEmpty(), settings.getMappingVersion(), errors, METRICS);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public DataValueSet export(String period) {
        Configuration configuration = configuration();
        if (!configuration.ready()) throw new IllegalArgumentException(String.join("; ", configuration.errors()));
        YearMonth month;
        try {
            if (period == null || !period.matches("\\d{6}")) throw new IllegalArgumentException();
            month = YearMonth.parse(period, DateTimeFormatter.ofPattern("uuuuMM"));
        } catch (RuntimeException exception) { throw new IllegalArgumentException("Use a valid monthly period in YYYYMM format"); }
        if (month.getYear() < 2010 || month.isAfter(YearMonth.now(ZoneId.of("Africa/Nairobi")))) throw new IllegalArgumentException("Choose a current or historical reporting month from 2010 onwards");
        var summary = reporting.summarize(month.atDay(1), month.atEndOfMonth());
        Map<String, Long> counts = Map.of("registrationsRecorded", summary.registeredPatients(), "encountersRecorded", summary.visits(),
                "appointmentsRecorded", summary.appointments(), "prescriptionsRecorded", summary.prescriptions(),
                "admissionsRecorded", summary.inpatientAdmissions(), "dischargesRecorded", summary.inpatientDischarges());
        List<DataValue> values = settings.getMappings().stream().map(mapping -> new DataValue(mapping.getDataElement(),
                mapping.getCategoryOptionCombo(), Long.toString(counts.get(mapping.getMetric())))).toList();
        return new DataValueSet(settings.getDataSet(), period, settings.getOrgUnit(), settings.getAttributeOptionCombo(), values);
    }

    private boolean uid(String value) { return value != null && value.matches("[A-Za-z][A-Za-z0-9]{10}"); }
    public record Configuration(boolean ready, String mappingVersion, List<String> errors, Map<String, String> availableMetrics) {}
    public record DataValue(String dataElement, String categoryOptionCombo, String value) {}
    public record DataValueSet(String dataSet, String period, String orgUnit, String attributeOptionCombo, List<DataValue> dataValues) {}
}