package com.afyaquik.communication;

import com.afyaquik.patients.entity.Patient;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_messages")
@Getter @Setter @NoArgsConstructor
public class PatientMessage extends SuperEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Patient patient;
    @Column(nullable = false, unique = true)
    private UUID clientReference;
    @Column(nullable = false)
    private String template;
    private Long sourceId;
    private String recipient;
    @Column(length = 640, nullable = false)
    private String content;
    @Column(nullable = false)
    private String status = "QUEUED";
    private String queuedBy;
    private String consentSource;
    private LocalDateTime consentAt;
    private String sentBy;
    private LocalDateTime attemptedAt;
    private String providerMessageId;
    private String failure;
}