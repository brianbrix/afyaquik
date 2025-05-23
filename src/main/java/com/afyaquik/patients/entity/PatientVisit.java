package com.afyaquik.patients.entity;

import com.afyaquik.utils.SuperEntity;
import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVisit extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    private String summaryReasonForVisit;

    private LocalDate visitDate;
    private LocalDate nextVisitDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "triage_report_id")
    private TriageReport triageReport;

    @OneToMany(mappedBy = "patientVisit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PatientAttendingPlan> patientAttendingPlan= new ArrayList<>();

    @OneToMany(mappedBy = "patientVisit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PatientVisitNotes> patientVisitNotes;

    @Enumerated(EnumType.STRING)
    private VisitStatus visitStatus=VisitStatus.DOCTOR;




}
