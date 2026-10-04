package com.afyaquik.inpatient;

import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "inpatient_admissions")
@Getter @Setter @NoArgsConstructor
public class InpatientAdmission extends SuperEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PatientVisit patientVisit;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private InpatientBed bed;
    @Column(unique = true)
    private Long activePatientKey;
    private String status = "ADMITTED";
    private LocalDateTime admittedAt;
    private String admittedBy;
    @Column(length = 1000)
    private String admissionReason;
    @Column(length = 4000)
    private String dischargeSummary;
    private LocalDateTime dischargeOrderedAt;
    private String dischargeOrderedBy;
    private LocalDateTime dischargedAt;
    private String dischargedBy;
    @ElementCollection
    @CollectionTable(name = "inpatient_admission_events")
    @OrderColumn(name = "event_order")
    private List<Event> events = new ArrayList<>();

    @Embeddable @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Event {
        private String action;
        private String fromBed;
        private String toBed;
        private LocalDateTime occurredAt;
        private String actor;
        @Column(length = 1000)
        private String reason;
    }
}