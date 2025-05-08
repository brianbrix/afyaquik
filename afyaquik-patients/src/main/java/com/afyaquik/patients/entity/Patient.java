package com.afyaquik.patients.entity;

import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.afyaquik.users.entity.ContactInfo;

import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String secondName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String dateOfBirth;
    private String nationalId;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

    @OneToMany
    private List<PatientVisit> patientVisit;
}
