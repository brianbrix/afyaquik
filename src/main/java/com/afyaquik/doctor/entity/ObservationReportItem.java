package com.afyaquik.doctor.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;


@Entity
@Table(name = "observation_report_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationReportItem extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "observation_report_id")
    private ObservationReport observationReport;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "observation_item_id")
    private ObservationItem item;
    @Column(length = 10000, columnDefinition = "TEXT")
    private String value;
    @Column(length = 3000, columnDefinition = "TEXT")
    private String comment;

}
