package com.afyaquik.patients.entity;

import com.afyaquik.dtos.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "triage_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriageItem extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "triageItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TriageReportItem> triageReportItems;
}
