package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.user.ContactInfo;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientAttendingPlan;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import com.afyaquik.patients.repository.PatientAttendingPlanRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientVisitRepo patientVisitRepository;

    @Mock
    private PatientAttendingPlanRepo patientAttendingPlanRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private PatientDto patientDto;
    private PatientVisit patientVisit;
    private PatientAttendingPlan patientAttendingPlan;
    private User user;

    @BeforeEach
    public void setUp() {
        // Setup common test data
        com.afyaquik.users.entity.ContactInfo contactInfo = new com.afyaquik.users.entity.ContactInfo();
        contactInfo.setPhoneNumber("1234567890");
        contactInfo.setPhoneNumber2("0987654321");
        contactInfo.setEmail("test@example.com");
        contactInfo.setAddress("123 Test St");

        patient = Patient.builder()
                .id(1L)
                .firstName("John")
                .secondName("Doe")
                .lastName("Smith")
                .gender(Gender.MALE)
                .dateOfBirth("1990-01-01")
                .nationalId("12345678")
                .maritalStatus(MaritalStatus.SINGLE)
                .contactInfo(contactInfo)
                .patientVisit(new ArrayList<>())
                .build();

        patientDto = PatientDto.builder()
                .id(1L)
                .firstName("John")
                .secondName("Doe")
                .lastName("Smith")
                .gender("MALE")
                .dateOfBirth("1990-01-01")
                .nationalId("12345678")
                .maritalStatus("SINGLE")
                .contactInfo(ContactInfo.builder()
                        .phone("1234567890")
                        .phone2("0987654321")
                        .email("test@example.com")
                        .address("123 Test St")
                        .build())
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        patientVisit = PatientVisit.builder()
                .id(1L)
                .patient(patient)
                .visitType(VisitType.CONSULTATION)
                .summaryReasonForVisit("Test visit")
                .visitDate(LocalDateTime.now())
                .visitStatus(VisitStatus.RECEPTION)
                .build();

        patientAttendingPlan = PatientAttendingPlan.builder()
                .id(1L)
                .attendingOfficer(user)
                .officerRole("DOCTOR")
                .assignedOfficer(user)
                .assignedOfficerRole("NURSE")
                .patientVisit(patientVisit)
                .build();

        patientVisit.setPatientAttendingPlan(patientAttendingPlan);
        patient.setPatientVisit(List.of(patientVisit));
    }

    @Test
    public void createPatient_Success() {
        // Arrange
        PatientDto inputDto = PatientDto.builder()
                .firstName("John")
                .secondName("Doe")
                .lastName("Smith")
                .gender("MALE")
                .dateOfBirth("1990-01-01")
                .nationalId("12345678")
                .maritalStatus("SINGLE")
                .contactInfo(ContactInfo.builder()
                        .phone("1234567890")
                        .phone2("0987654321")
                        .email("test@example.com")
                        .address("123 Test St")
                        .build())
                .build();

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient savedPatient = invocation.getArgument(0);
            savedPatient.setId(1L);
            return savedPatient;
        });

        // Act
        PatientDto result = patientService.createPatient(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSecondName());
        assertEquals("Smith", result.getLastName());
        assertEquals("MALE", result.getGender());
        assertEquals("1990-01-01", result.getDateOfBirth());
        assertEquals("12345678", result.getNationalId());
        assertEquals("SINGLE", result.getMaritalStatus());
        assertNotNull(result.getContactInfo());
        assertEquals("1234567890", result.getContactInfo().getPhone());

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    public void getPatient_Success() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientDto result = patientService.getPatient(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSecondName());
        assertEquals("Smith", result.getLastName());

        verify(patientRepository).findById(1L);
    }

    @Test
    public void getPatient_NotFound() {
        // Arrange
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.getPatient(999L));
        verify(patientRepository).findById(999L);
    }

    @Test
    public void updatePatient_Success() {
        // Arrange
        PatientDto updateDto = PatientDto.builder()
                .id(1L)
                .firstName("Jane")
                .secondName("Doe")
                .lastName("Smith")
                .gender("FEMALE")
                .dateOfBirth("1990-01-01")
                .nationalId("12345678")
                .maritalStatus("MARRIED")
                .contactInfo(ContactInfo.builder()
                        .phone("1234567890")
                        .phone2("0987654321")
                        .email("updated@example.com")
                        .address("456 Test St")
                        .build())
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        PatientDto result = patientService.updatePatient(updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(patientRepository).findById(1L);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    public void updatePatient_NotFound() {
        // Arrange
        PatientDto updateDto = PatientDto.builder()
                .id(999L)
                .firstName("Jane")
                .secondName("Doe")
                .lastName("Smith")
                .gender("FEMALE")
                .dateOfBirth("1990-01-01")
                .nationalId("12345678")
                .maritalStatus("MARRIED")
                .build();

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.updatePatient(updateDto));
        verify(patientRepository).findById(999L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    public void deletePatient_Success() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(any(Patient.class));

        // Act
        patientService.deletePatient(1L);

        // Assert
        verify(patientRepository).findById(1L);
        verify(patientRepository).delete(patient);
    }

    @Test
    public void deletePatient_NotFound() {
        // Arrange
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.deletePatient(999L));
        verify(patientRepository).findById(999L);
        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    public void filterPatients_Success() {
        // Arrange
        PatientDto filterDto = PatientDto.builder()
                .firstName("John")
                .build();

        when(patientRepository.findAll(any(Specification.class))).thenReturn(List.of(patient));

        // Act
        List<PatientDto> results = patientService.filterPatients(filterDto);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("John", results.get(0).getFirstName());

        verify(patientRepository).findAll(any(Specification.class));
    }

    @Test
    public void getPatientVisits_Success() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        List<PatientVisitDto> results = patientService.getPatientVisits(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("Test visit", results.get(0).getSummaryReasonForVisit());

        verify(patientRepository).findById(1L);
    }

    @Test
    public void getPatientVisits_NotFound() {
        // Arrange
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.getPatientVisits(999L));
        verify(patientRepository).findById(999L);
    }

    @Test
    public void createPatientAttendingPlan_Success() {
        // Arrange
        PatientAttendingPlanDto planDto = PatientAttendingPlanDto.builder()
                .patientVisitId(1L)
                .role("DOCTOR")
                .assignedOfficer("testuser")
                .assignedOfficerRole("NURSE")
                .build();

        when(patientVisitRepository.findById(1L)).thenReturn(Optional.of(patientVisit));
        when(userService.getCurrentUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(patientVisitRepository.save(any(PatientVisit.class))).thenReturn(patientVisit);

        // Act
        PatientAttendingPlanDto result = patientService.createPatientAttendingPlan(planDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPatientVisitId());

        verify(patientVisitRepository).findById(1L);
        verify(userService, times(2)).findByUsername("testuser");
        verify(patientVisitRepository).save(any(PatientVisit.class));
    }

    @Test
    public void createPatientAttendingPlan_VisitNotFound() {
        // Arrange
        PatientAttendingPlanDto planDto = PatientAttendingPlanDto.builder()
                .patientVisitId(999L)
                .role("DOCTOR")
                .assignedOfficer("testuser")
                .assignedOfficerRole("NURSE")
                .build();

        when(patientVisitRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.createPatientAttendingPlan(planDto));
        verify(patientVisitRepository).findById(999L);
        verify(patientVisitRepository, never()).save(any(PatientVisit.class));
    }

    @Test
    public void updatePatientAttendingPlan_Success() {
        // Arrange
        PatientAttendingPlanDto planDto = PatientAttendingPlanDto.builder()
                .id(1L)
                .role("DOCTOR")
                .assignedOfficer("testuser")
                .assignedOfficerRole("NURSE")
                .build();

        when(patientAttendingPlanRepo.findById(1L)).thenReturn(Optional.of(patientAttendingPlan));
        when(userService.getCurrentUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(patientAttendingPlanRepo.save(any(PatientAttendingPlan.class))).thenReturn(patientAttendingPlan);

        // Act
        PatientAttendingPlanDto result = patientService.updatePatientAttendingPlan(planDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getAttendingOfficerUserName());

        verify(patientAttendingPlanRepo).findById(1L);
        verify(userService, times(2)).findByUsername("testuser");
        verify(patientAttendingPlanRepo).save(any(PatientAttendingPlan.class));
    }

    @Test
    public void updatePatientAttendingPlan_NotFound() {
        // Arrange
        PatientAttendingPlanDto planDto = PatientAttendingPlanDto.builder()
                .id(999L)
                .role("DOCTOR")
                .assignedOfficer("testuser")
                .assignedOfficerRole("NURSE")
                .build();

        when(patientAttendingPlanRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.updatePatientAttendingPlan(planDto));
        verify(patientAttendingPlanRepo).findById(999L);
        verify(patientAttendingPlanRepo, never()).save(any(PatientAttendingPlan.class));
    }
}
