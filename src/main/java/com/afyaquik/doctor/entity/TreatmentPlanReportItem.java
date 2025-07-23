package com.afyaquik.doctor.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treatment_plan_report_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentPlanReportItem extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_plan_item_id")
    private TreatmentPlanItem treatmentPlanItem;
    @Column(columnDefinition = "TEXT", length = 10000)
    private String reportDetails;
}
