package com.afyaquik.patients.entity;

import com.afyaquik.utils.SuperEntity;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "patient_visit_plans",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"assigned_officer_id", "next_station_id", "visit_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAttendingPlan extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attending_officer_id")
    private User attendingOfficer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_station_id", nullable = false)
    private Station nextStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id", nullable = false)
    private User assignedOfficer;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private PatientVisit patientVisit;
}
