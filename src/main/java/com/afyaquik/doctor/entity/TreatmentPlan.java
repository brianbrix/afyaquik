package com.afyaquik.doctor.entity;

import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "treatment_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentPlan extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT" ,length = 1000)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_visit_id")
    private PatientVisit patientVisit;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "treatmentPlan")
    private List<TreatmentPlanReportItem> treatmentPlanReportItems;

}
