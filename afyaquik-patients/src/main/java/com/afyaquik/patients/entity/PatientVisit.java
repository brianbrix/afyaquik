package com.afyaquik.patients.entity;

import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patient_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @CreatedBy
    private User createdBy;

    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    private String summaryReasonForVisit;

    private LocalDateTime  visitDate;
    private LocalDateTime nextVisitDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "triage_report_id", nullable = false)
    private TriageReport triageReport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attending_plan_id", nullable = false)
    private PatientAttendingPlan patientAttendingPlan;

    @OneToMany(mappedBy = "patientVisit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientVisitNotes> patientVisitNotes;

    @Enumerated(EnumType.STRING)
    private VisitStatus visitStatus;




}
