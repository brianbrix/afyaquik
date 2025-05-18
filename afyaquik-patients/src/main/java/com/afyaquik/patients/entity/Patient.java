package com.afyaquik.patients.entity;

import com.afyaquik.dtos.SuperEntity;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import jakarta.persistence.*;
import jakarta.persistence.FetchType;
import lombok.*;
import com.afyaquik.users.entity.ContactInfo;

import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String secondName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender=Gender.MALE;
    private String dateOfBirth;
    private String nationalId;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "patient")
    private List<PatientVisit> patientVisit;

    public String getPatientName() {
        StringBuilder stringBuilder = new StringBuilder();
        if (firstName != null) {
            stringBuilder.append(firstName);
        }
        if (secondName != null) {
            stringBuilder.append(" ").append(secondName);
        }
        if (lastName != null) {
            stringBuilder.append(" ").append(lastName);
        }
        return stringBuilder.toString().trim();
    }
}
