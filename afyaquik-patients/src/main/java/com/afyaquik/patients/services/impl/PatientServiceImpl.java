package com.afyaquik.patients.services.impl;

import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.patient.TriageReportDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientAttendingPlan;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import com.afyaquik.patients.enums.VisitStatus;
import com.afyaquik.patients.enums.VisitType;
import com.afyaquik.patients.repository.PatientAttendingPlanRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.repository.TriageReportRepository;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.patients.utils.PatientSpecifications;
import com.afyaquik.users.entity.ContactInfo;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final PatientAttendingPlanRepo  patientAttendingPlanRepo;
    private final UserService userService;

    private static PatientDto buildPatientDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .secondName(patient.getSecondName())
                .lastName(patient.getLastName())
                .gender(patient.getGender().name())
                .dateOfBirth(patient.getDateOfBirth())
                .nationalId(patient.getNationalId())
                .maritalStatus(patient.getMaritalStatus().name())
                .contactInfo(com.afyaquik.dtos.user.ContactInfo.builder()
                        .phone(patient.getContactInfo().getPhoneNumber())
                        .phone2(patient.getContactInfo().getPhoneNumber2())
                        .email(patient.getContactInfo().getEmail())
                        .address(patient.getContactInfo().getAddress())
                        .build())
                .build();
    }

    @Override
    public PatientDto createPatient(PatientDto dto) {
        Patient patient = new Patient();

        patient.setFirstName(dto.getFirstName());
        patient.setSecondName(dto.getSecondName());
        patient.setLastName(dto.getLastName());

        if (dto.getGender() != null) {
            patient.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        }

        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setNationalId(dto.getNationalId());

        if (dto.getMaritalStatus() != null) {
            patient.setMaritalStatus(MaritalStatus.valueOf(dto.getMaritalStatus().toUpperCase()));
        }

        if (dto.getContactInfo() != null) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setPhoneNumber(dto.getContactInfo().getPhone());
            contactInfo.setPhoneNumber2(dto.getContactInfo().getPhone2());
            contactInfo.setEmail(dto.getContactInfo().getEmail());
            contactInfo.setAddress(dto.getContactInfo().getAddress());
            patient.setContactInfo(contactInfo);
        }
        patient = patientRepository.save(patient);
        dto.setId(patient.getId());
        return dto;
    }

    @Override
    public PatientDto getPatient(Long id) {
        return patientRepository.findById(id).map(PatientServiceImpl::buildPatientDto).orElseThrow(()->new EntityNotFoundException("Patient not found"));
    }

    @Override
    public PatientDto updatePatient(PatientDto patientDto) {
        Patient patient = patientRepository.findById(patientDto.getId()).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        patient.setFirstName(patientDto.getFirstName());
        patient.setSecondName(patientDto.getSecondName());
        patient.setLastName(patientDto.getLastName());
        patient.setGender(Gender.valueOf(patientDto.getGender().toUpperCase()));
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setNationalId(patientDto.getNationalId());
        patient.setMaritalStatus(MaritalStatus.valueOf(patientDto.getMaritalStatus().toUpperCase()));
        if (patientDto.getContactInfo() != null) {
            ContactInfo contactInfo = patient.getContactInfo();
            contactInfo.setPhoneNumber(patientDto.getContactInfo().getPhone());
            contactInfo.setPhoneNumber2(patientDto.getContactInfo().getPhone2());
            contactInfo.setEmail(patientDto.getContactInfo().getEmail());
            contactInfo.setAddress(patientDto.getContactInfo().getAddress());
            patient.setContactInfo(contactInfo);
        }
        patient = patientRepository.save(patient);
        return buildPatientDto(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        patientRepository.delete(patient);
    }

    @Override
    public List<PatientDto> filterPatients(PatientDto dto) {
        List<Patient> patients = patientRepository.findAll(PatientSpecifications.filterByPatientDto(dto));
        return patients.stream().map(PatientServiceImpl::buildPatientDto).toList();
    }

    @Override
    public List<PatientVisitDto> getPatientVisits(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        return patient.getPatientVisit().stream().map(visit -> PatientVisitDto.builder()
                .id(visit.getId())
                .patientId(visit.getPatient().getId())
                .summaryReasonForVisit(visit.getSummaryReasonForVisit())
                .visitDate(visit.getVisitDate())
                .visitType(visit.getVisitType().name())
                .build()).toList();
    }


    @Override
    public PatientAttendingPlanDto createPatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto) {
        PatientVisit  patientVisit = patientVisitRepository.findById(patientAttendingPlanDto.getPatientVisitId())
            .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));

    PatientAttendingPlan  patientAttendingPlan = PatientAttendingPlan.builder()
            .patientVisit(patientVisit)
            .build();
        String userName = userService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAttendingPlan.setAttendingOfficer(attendingOfficer);
        patientAttendingPlan.setOfficerRole(patientAttendingPlanDto.getRole());
        patientAttendingPlan.setAssignedOfficer(userService.findByUsername(patientAttendingPlanDto.getAssignedOfficer()));
        patientAttendingPlan.setAssignedOfficerRole(patientAttendingPlanDto.getAssignedOfficerRole());
        patientVisit.setPatientAttendingPlan(patientAttendingPlan);
        patientVisitRepository.save(patientVisit);

        return PatientAttendingPlanDto.builder()
            .id(patientAttendingPlan.getId())
            .patientVisitId(patientAttendingPlan.getPatientVisit().getId())
            .build();

    }

    @Override
    public PatientAttendingPlanDto updatePatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto) {
       PatientAttendingPlan  patientAttendingPlan = patientAttendingPlanRepo.findById(patientAttendingPlanDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient attending plan not found"));
        patientAttendingPlan.setPatientVisit(patientAttendingPlan.getPatientVisit());

        String userName = userService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAttendingPlan.setAttendingOfficer(attendingOfficer);
        patientAttendingPlan.setOfficerRole(patientAttendingPlanDto.getRole());
        patientAttendingPlan.setAssignedOfficer(userService.findByUsername(patientAttendingPlanDto.getAssignedOfficer()));
        patientAttendingPlan.setAssignedOfficerRole(patientAttendingPlanDto.getAssignedOfficerRole());

        patientAttendingPlanRepo.save(patientAttendingPlan);
        return PatientAttendingPlanDto.builder()
            .id(patientAttendingPlan.getId())
            .patientVisitId(patientAttendingPlan.getPatientVisit().getId())
            .attendingOfficerUserName(patientAttendingPlan.getAttendingOfficer().getUsername())
            .build();
    }


}
