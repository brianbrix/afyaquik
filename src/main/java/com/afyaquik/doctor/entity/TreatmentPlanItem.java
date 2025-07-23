package com.afyaquik.doctor.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treatment_plan_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentPlanItem extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(columnDefinition = "TEXT", length = 10000)
    private String doctorNotes;
    private boolean enabled=true;
}
