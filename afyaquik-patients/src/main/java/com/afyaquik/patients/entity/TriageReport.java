package com.afyaquik.patients.entity;

import com.afyaquik.dtos.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "triage_report_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageReport extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "triageReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TriageReportItem> triageReportItems;

    @OneToOne(mappedBy = "triageReport")
    private PatientVisit patientVisit;
}
