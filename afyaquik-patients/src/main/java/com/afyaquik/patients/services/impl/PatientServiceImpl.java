package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.mappers.PatientVisitMapper;
import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientAttendingPlan;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import com.afyaquik.patients.repository.PatientAttendingPlanRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.users.entity.ContactInfo;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientVisitRepo patientVisitRepository;
    private final StationRepository stationRepository;
    private final PatientAttendingPlanRepo  patientAttendingPlanRepo;
    private final UserService userService;
    private final PatientVisitMapper patientVisitMapper;

    private static PatientDto buildPatientDto(Patient patient) {
        PatientDto patientDto = PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .secondName(patient.getSecondName())
                .lastName(patient.getLastName())
                .gender(patient.getGender()!=null?patient.getGender().name():null)
                .dateOfBirth(patient.getDateOfBirth())
                .nationalId(patient.getNationalId())
                .maritalStatus(patient.getMaritalStatus()!=null?patient.getMaritalStatus().name():null)
                .build();
        if (patient.getContactInfo() != null)
        {
            patientDto.setContactInfo(com.afyaquik.dtos.user.ContactInfo.builder()
                .phone(patient.getContactInfo().getPhoneNumber())
                .phone2(patient.getContactInfo().getPhoneNumber2())
                .email(patient.getContactInfo().getEmail())
                .address(patient.getContactInfo().getAddress())
                .build());
        }
        return patientDto;
    }
    @Transactional
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
        return patientRepository.findByIdWithVisits(id).map(PatientServiceImpl::buildPatientDto).orElseThrow(()->new EntityNotFoundException("Patient not found"));
    }
    @Transactional
    @Override
    public PatientDto updatePatient(PatientDto patientDto) {
        Patient patient = patientRepository.findById(patientDto.getId()).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        patient.setFirstName(patientDto.getFirstName());
        patient.setSecondName(patientDto.getSecondName());
        patient.setLastName(patientDto.getLastName());
        if (patientDto.getGender() != null)
        {
            patient.setGender(Gender.valueOf(patientDto.getGender()));
        }
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setNationalId(patientDto.getNationalId());
        if (patientDto.getMaritalStatus() != null) {
            patient.setMaritalStatus(MaritalStatus.valueOf(patientDto.getMaritalStatus()));
        }
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
//        List<Patient> patients = patientRepository.findAll(PatientSpecifications.filterByPatientDto(dto));
        return null;
    }

    @Override
    public ListFetchDto<PatientVisitDto> getPatientVisits(Pageable pageable, Long patientId) {
        Patient patient = patientRepository.findByIdWithVisits(patientId).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        Page<PatientVisit> patientVisits = patientVisitRepository.findAllByPatient(pageable, patient);

        return ListFetchDto.<PatientVisitDto>builder()
                .results(patientVisits.map(patientVisitMapper::toDto))
                .build();

    }

    @Transactional
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
        Station nextStation = stationRepository.findByName(patientAttendingPlanDto.getNextStation()).
                orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAttendingPlanDto.getAssignedOfficer());
        patientAttendingPlan.setNextStation(nextStation);
        patientAttendingPlan.setAssignedOfficer(assignedOfficer);
        patientVisit.getPatientAttendingPlan().add(patientAttendingPlan);
        patientVisitRepository.save(patientVisit);

        return PatientAttendingPlanDto.builder()
                .id(patientAttendingPlan.getId())
                .patientName(patientVisit.getPatient().getPatientName())
                .nextStation(patientAttendingPlan.getNextStation().getName())
                .assignedOfficer(patientAttendingPlan.getAssignedOfficer().getUsername())
                .patientVisitId(patientAttendingPlan.getPatientVisit().getId())
                .attendingOfficerUserName(patientAttendingPlan.getAttendingOfficer().getUsername())
                .build();

    }

    @Transactional
    @Override
    public PatientAttendingPlanDto updatePatientAttendingPlan(PatientAttendingPlanDto patientAttendingPlanDto) {
       PatientAttendingPlan  patientAttendingPlan = patientAttendingPlanRepo.findById(patientAttendingPlanDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient attending plan not found"));
        patientAttendingPlan.setPatientVisit(patientAttendingPlan.getPatientVisit());

        String userName = userService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAttendingPlan.setAttendingOfficer(attendingOfficer);
        Station nextStation = stationRepository.findByName(patientAttendingPlanDto.getNextStation()).orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAttendingPlanDto.getAssignedOfficer());
        patientAttendingPlan.setNextStation(nextStation);
        patientAttendingPlan.setAssignedOfficer(assignedOfficer);
        patientAttendingPlan.setAssignedOfficer(userService.findByUsername(patientAttendingPlanDto.getAssignedOfficer()));

        patientAttendingPlan = patientAttendingPlanRepo.save(patientAttendingPlan);
        return PatientAttendingPlanDto.builder()
            .id(patientAttendingPlan.getId())
                .patientName(patientAttendingPlan.getPatientVisit().getPatient().getPatientName())
                .nextStation(patientAttendingPlan.getNextStation().getName())
                .assignedOfficer(patientAttendingPlan.getAssignedOfficer().getUsername())
            .patientVisitId(patientAttendingPlan.getPatientVisit().getId())
            .attendingOfficerUserName(patientAttendingPlan.getAttendingOfficer().getUsername())
            .build();
    }


}
