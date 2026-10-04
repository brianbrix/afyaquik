package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.users.dto.ContactInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientRegistrationTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final PatientServiceImpl service = new PatientServiceImpl(patients, null, null, null, null, null, null, null);

    @BeforeEach
    void setUp() {
        when(patients.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @ParameterizedTest
    @CsvSource({
            "0712345678, +254712345678",
            "0112345678, +254112345678",
            "254712345678, +254712345678",
            "+254712345678, +254712345678",
            "0712 345 678, +254712345678",
            "+447123456789, +447123456789"
    })
    void storesNormalizedOptionalPhoneNumbers(String input, String expected) {
        PatientDto request = PatientDto.builder().firstName("Amina")
                .contactInfo(ContactInfo.builder().phoneNumber(input).build()).build();
        assertEquals(expected, service.createPatient(request).getContactInfo().getPhoneNumber());
    }

    @Test
    void childrenCanBeRegisteredWithoutIdentificationOrPhone() {
        PatientDto request = PatientDto.builder().firstName("Child").gender("").maritalStatus("").nationalId("").build();
        service.createPatient(request);
        assertNull(request.getNationalId());
        assertNull(request.getGender());
        verify(patients).save(any());
    }

    @Test
    void rejectsImpossibleBirthDateBeforeWriting() {
        assertThrows(IllegalArgumentException.class, () -> service.createPatient(
                PatientDto.builder().firstName("Patient").dateOfBirth("2025-02-30").build()));
        verify(patients, never()).save(any());
    }

    @Test
    void rejectsFutureBirthDateBeforeWriting() {
        assertThrows(IllegalArgumentException.class, () -> service.createPatient(
                PatientDto.builder().firstName("Patient").dateOfBirth("2099-01-01").build()));
        verify(patients, never()).save(any());
    }

    @Test
    void deletingPatientPreservesClinicalHistory() {
        Patient patient = new Patient();
        when(patients.findForUpdate(1L)).thenReturn(Optional.of(patient));
        service.deletePatient(1L);
        assertTrue(patient.isDeleted());
        verify(patients).save(patient);
        verify(patients, never()).delete(any());
    }

    @Test
    void activeClinicalCarePreventsPatientArchive() {
        Patient patient = new Patient();
        patient.setPatientVisit(java.util.List.of(com.afyaquik.patients.entity.PatientVisit.builder().build()));
        when(patients.findForUpdate(1L)).thenReturn(Optional.of(patient));
        assertThrows(IllegalArgumentException.class, () -> service.deletePatient(1L));
        assertFalse(patient.isDeleted());
        verify(patients, never()).save(any());
    }
}