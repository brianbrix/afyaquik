package com.afyaquik.appointments.entity;

import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.utils.SuperEntity;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "appointmentDateTime"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDateTime;

    @Column(length = 1000, columnDefinition = "TEXT")
    private String reason;

    @Column(length = 5000, columnDefinition = "TEXT")
    private String notes;


    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
}
