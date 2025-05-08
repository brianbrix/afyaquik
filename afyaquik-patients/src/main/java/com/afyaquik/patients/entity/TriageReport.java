package com.afyaquik.patients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "triage_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "triageReport")
    private PatientVisit patientVisit;
    private String bpReport;
    private String temperatureReport;
    private String pulseReport;
    private String weightReport;
    private String heightReport;
    private String bmiReport;
    private String respiratoryRateReport;
    private String oxygenSaturationReport;
    private String bloodSugarReport;
    @Column(columnDefinition = "TEXT" ,length = 2000)
    private String complaintSummaryReport;
}
