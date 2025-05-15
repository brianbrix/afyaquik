package com.afyaquik.patients.entity;

import com.afyaquik.dtos.SuperEntity;
import com.afyaquik.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

@Entity
@Table(name = "patient_visit_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVisitNotes extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private PatientVisit patientVisit;


    @Column(columnDefinition = "TEXT", length = 3000)
    private String notes;
}
