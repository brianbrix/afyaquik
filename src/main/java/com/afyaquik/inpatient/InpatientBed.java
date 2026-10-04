package com.afyaquik.inpatient;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inpatient_beds", uniqueConstraints = @UniqueConstraint(columnNames = {"ward", "code"}))
@Getter @Setter @NoArgsConstructor
public class InpatientBed extends SuperEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String ward;
    @Column(nullable = false, length = 30)
    private String code;
    private boolean active = true;
    @OneToOne(fetch = FetchType.LAZY)
    private InpatientAdmission currentAdmission;
}