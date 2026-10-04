package com.afyaquik.reports;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityReportingService {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Summary summarize(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from) || ChronoUnit.DAYS.between(from, to) > 366) {
            throw new IllegalArgumentException("Choose a valid reporting period of at most 366 days");
        }
        Map<String, Long> visits = statuses("PatientVisit", "visitStatus", "visitDate", from, to, true);
        Map<String, Long> appointments = statuses("Appointment", "status", "appointmentDateTime", from, to, false);
        return new Summary(from, to, count("Patient", from, to),
                visits.values().stream().mapToLong(Long::longValue).sum(),
                appointments.values().stream().mapToLong(Long::longValue).sum(), count("PatientDrug", from, to), visits, appointments,
                count("InpatientAdmission", "admittedAt", from, to), count("InpatientAdmission", "dischargedAt", from, to));
    }

    private long count(String entity, LocalDate from, LocalDate to) {
        return count(entity, "createdAt", from, to);
    }

    private long count(String entity, String date, LocalDate from, LocalDate to) {
        return entityManager.createQuery("select count(record) from " + entity
                        + " record where record.deleted = false and record." + date + " >= :from and record." + date + " < :until", Long.class)
                .setParameter("from", from.atStartOfDay()).setParameter("until", to.plusDays(1).atStartOfDay()).getSingleResult();
    }

    private Map<String, Long> statuses(String entity, String status, String date, LocalDate from, LocalDate to, boolean dateOnly) {
        String interval = dateOnly ? "record." + date + " between :from and :until"
                : "record." + date + " >= :from and record." + date + " < :until";
        var query = entityManager.createQuery("select record." + status + ", count(record) from " + entity
                + " record where record.deleted = false and " + interval + " group by record." + status, Object[].class);
        query.setParameter("from", dateOnly ? from : from.atStartOfDay());
        query.setParameter("until", dateOnly ? to : to.plusDays(1).atStartOfDay());
        Map<String, Long> results = new LinkedHashMap<>();
        for (Object[] row : query.getResultList()) {
            results.put(row[0] == null ? "UNSPECIFIED" : row[0].toString(), ((Number) row[1]).longValue());
        }
        return results;
    }

    public record Summary(LocalDate from, LocalDate to, long registeredPatients, long visits, long appointments,
                          long prescriptions, Map<String, Long> visitsByStatus, Map<String, Long> appointmentsByStatus,
                          long inpatientAdmissions, long inpatientDischarges) {}
}