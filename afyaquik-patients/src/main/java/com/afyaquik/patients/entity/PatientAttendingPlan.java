package com.afyaquik.patients.entity;

import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_visit_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAttendingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attending_officer_id")
    private User attendingOfficer;

    private String officerRole;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    private String assignedOfficerRole;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "patientAttendingPlan")
    private PatientVisit patientVisit;

}
