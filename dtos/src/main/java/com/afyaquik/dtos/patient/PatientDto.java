package com.afyaquik.dtos.patient;

import com.afyaquik.dtos.user.ContactInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
        private Long id;
        private String firstName;
        private String secondName;
        private String lastName;
        private String gender;
        private String dateOfBirth;
        private String nationalId;
        private String maritalStatus;
        private ContactInfo contactInfo;
}
