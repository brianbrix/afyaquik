package com.afyaquik.dtos.patient;

import com.afyaquik.dtos.user.ContactInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
        private Long id;
        @NotBlank(message = "First name is required")
        private String firstName;
        private String secondName;
        private String lastName;
        private String gender;
        private String dateOfBirth;
        private LocalDateTime createdAt;
        private String nationalId;
        private String maritalStatus;
        private ContactInfo contactInfo;
}
