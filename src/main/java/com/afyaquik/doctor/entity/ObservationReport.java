package com.afyaquik.doctor.entity;

import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.users.entity.User;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "observation_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationReport extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_visit_id")
    private PatientVisit patientVisit;

    @OneToMany(mappedBy = "observationReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ObservationReportItem> observationReportItems;
}
