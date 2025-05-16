package com.afyaquik.patients.entity;

import com.afyaquik.dtos.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "triage_report_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageReportItem extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "triage_item_id")
    private TriageItem  triageItem;

    @Column(columnDefinition = "TEXT" ,length = 2000, nullable = false)
    private String itemSummary;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "triage_report_id")
    private TriageReport  triageReport;


}
