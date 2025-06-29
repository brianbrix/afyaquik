package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.PatientAssignmentsDto;
import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.users.service.SecurityService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientAssignments;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import com.afyaquik.utils.mappers.patients.PatientAssignmentsMapper;
import com.afyaquik.utils.mappers.patients.PatientVisitMapper;
import com.afyaquik.patients.repository.PatientAssignmentsRepo;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.services.PatientService;
import com.afyaquik.users.entity.ContactInfo;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
    private final PatientAssignmentsRepo  patientAssignmentsRepo;
    private final UserService userService;
    private final SecurityService securityService;
    private final PatientVisitMapper patientVisitMapper;
    private final PatientAssignmentsMapper patientAssignmentsMapper;

    private static PatientDto buildPatientDto(Patient patient) {
        PatientDto patientDto = PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .secondName(patient.getSecondName())
                .lastName(patient.getLastName())
                .createdAt(patient.getCreatedAt())
                .gender(patient.getGender()!=null?patient.getGender().name():null)
                .dateOfBirth(patient.getDateOfBirth())
                .nationalId(patient.getNationalId())
                .maritalStatus(patient.getMaritalStatus()!=null?patient.getMaritalStatus().name():null)
                .build();
        if (patient.getContactInfo() != null)
        {
            patientDto.setContactInfo(com.afyaquik.users.dto.ContactInfo.builder()
                .phoneNumber(patient.getContactInfo().getPhoneNumber())
                .phoneNumber2(patient.getContactInfo().getPhoneNumber2())
                .email(patient.getContactInfo().getEmail())
                .address(patient.getContactInfo().getAddress())
                .build());
        }
        return patientDto;
    }
    @Transactional
    @Override
    @CacheEvict(value = "searchResults", allEntries = true)
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
            contactInfo.setPhoneNumber(dto.getContactInfo().getPhoneNumber());
            contactInfo.setPhoneNumber2(dto.getContactInfo().getPhoneNumber2());
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
    @CacheEvict(value = "searchResults", allEntries = true)
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
            if (patient.getContactInfo() == null) {
                patient.setContactInfo(new ContactInfo());
            }
            ContactInfo contactInfo = patient.getContactInfo();
            contactInfo.setPhoneNumber(patientDto.getContactInfo().getPhoneNumber());
            contactInfo.setPhoneNumber2(patientDto.getContactInfo().getPhoneNumber2());
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
    public PatientAssignmentsDto createPatientAssignments(PatientAssignmentsDto patientAssignmentsDto) {
        PatientVisit  patientVisit = patientVisitRepository.findById(patientAssignmentsDto.getPatientVisitId())
            .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));

    PatientAssignments  patientAssignments = PatientAssignments.builder()
            .patientVisit(patientVisit)
            .build();
        String userName = securityService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAssignments.setAttendingOfficer(attendingOfficer);
        Station nextStation = stationRepository.findByName(patientAssignmentsDto.getNextStation()).
                orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAssignmentsDto.getAssignedOfficer());
        if (!assignedOfficer.isAvailable())
        {
            throw new IllegalArgumentException("Assigned officer is not available");
        }
       var plan = patientAssignmentsRepo.findByAssignedOfficerAndNextStationAndPatientVisit(assignedOfficer,nextStation,patientVisit);
       if (plan.isPresent())
       {
           throw new EntityExistsException("You cannot assign the same station and officer to one patient visit.");

       }
        patientAssignments.setNextStation(nextStation);
        patientAssignments.setAssignedOfficer(assignedOfficer);
        patientVisit.getPatientAssignments().add(patientAssignments);
        patientVisitRepository.save(patientVisit);

        return patientAssignmentsMapper.toDto(patientAssignments);

    }

    @Transactional
    @Override
    public PatientAssignmentsDto updatePatientAssignments(PatientAssignmentsDto patientAssignmentsDto) {
       PatientAssignments  patientAssignments = patientAssignmentsRepo.findById(patientAssignmentsDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient attending plan not found"));
        patientAssignments.setPatientVisit(patientAssignments.getPatientVisit());

        String userName = securityService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAssignments.setAttendingOfficer(attendingOfficer);
        Station nextStation = stationRepository.findByName(patientAssignmentsDto.getNextStation()).orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAssignmentsDto.getAssignedOfficer());
        patientAssignments.setNextStation(nextStation);
        patientAssignments.setAssignedOfficer(assignedOfficer);
        patientAssignments.setAssignedOfficer(userService.findByUsername(patientAssignmentsDto.getAssignedOfficer()));

        patientAssignments = patientAssignmentsRepo.save(patientAssignments);
        return patientAssignmentsMapper.toDto(patientAssignments);
    }


}
