package com.afyaquik.patients.entity;

import com.afyaquik.billing.entity.Billing;
import com.afyaquik.utils.SuperEntity;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.enums.VisitType;
import jakarta.persistence.*;
import lombok.*;
import org.mapstruct.Named;

import java.time.LocalDate;
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
    private List<PatientAssignment> patientAssignments= new ArrayList<>();

    @OneToMany(mappedBy = "patientVisit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PatientVisitNotes> patientVisitNotes;

    @Enumerated(EnumType.STRING)
    private Status visitStatus= Status.PENDING;

    @OneToOne(mappedBy = "patientVisit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Billing billing;

    public String getPatientName()
    {
        return this.patient.getFirstName() + " " + this.patient.getLastName();
    }




}
