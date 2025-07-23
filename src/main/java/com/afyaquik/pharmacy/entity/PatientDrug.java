package com.afyaquik.pharmacy.entity;

import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "patient_drugs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDrug extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_visit_id", nullable = false)
    private PatientVisit patientVisit;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    private Double quantity;
    @Column(nullable = false)
    @ColumnDefault("0.00")
    private Double price=0.00;

    private String dosageInstructions;
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean dispensed = false;
}
