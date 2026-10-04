package com.afyaquik.patients.services.impl;

import com.afyaquik.patients.dto.PatientAssignmentDto;
import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientAssignment;
import com.afyaquik.users.service.SecurityService;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Gender;
import com.afyaquik.patients.enums.MaritalStatus;
import com.afyaquik.patients.enums.Status;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;
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
        normalizePatient(dto);
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
        normalizePatient(patientDto);
        Patient patient = patientRepository.findForUpdate(patientDto.getId()).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
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
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findForUpdate(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        if (patient.getPatientVisit() != null && patient.getPatientVisit().stream().anyMatch(visit -> !visit.isDeleted()
                && visit.getVisitStatus() != Status.COMPLETED && visit.getVisitStatus() != Status.CANCELLED)) {
            throw new IllegalArgumentException("Close active encounters before archiving this patient");
        }
        patient.setDeleted(true);
        patientRepository.save(patient);
    }

    private void normalizePatient(PatientDto patient) {
        if (patient.getFirstName() == null || patient.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        patient.setFirstName(patient.getFirstName().trim());
        patient.setNationalId(blankToNull(patient.getNationalId()));
        patient.setGender(blankToNull(patient.getGender()));
        if (patient.getGender() != null) patient.setGender(patient.getGender().toUpperCase(Locale.ROOT));
        patient.setMaritalStatus(blankToNull(patient.getMaritalStatus()));
        if (patient.getMaritalStatus() != null) patient.setMaritalStatus(patient.getMaritalStatus().toUpperCase(Locale.ROOT));
        patient.setDateOfBirth(blankToNull(patient.getDateOfBirth()));
        if (patient.getDateOfBirth() != null) {
            try {
                LocalDate birthDate = LocalDate.parse(patient.getDateOfBirth());
                if (birthDate.isAfter(LocalDate.now(ZoneId.of("Africa/Nairobi")))) {
                    throw new IllegalArgumentException("Date of birth cannot be in the future");
                }
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Enter a valid date of birth", exception);
            }
        }
        if (patient.getContactInfo() != null) {
            patient.getContactInfo().setPhoneNumber(normalizePhone(patient.getContactInfo().getPhoneNumber()));
            patient.getContactInfo().setPhoneNumber2(normalizePhone(patient.getContactInfo().getPhoneNumber2()));
            patient.getContactInfo().setEmail(blankToNull(patient.getContactInfo().getEmail()));
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizePhone(String value) {
        if (value == null || value.isBlank()) return null;
        String number = value.replaceAll("[\\s()-]", "");
        if (number.matches("0[17][0-9]{8}")) number = "+254" + number.substring(1);
        else if (number.matches("254[17][0-9]{8}")) number = "+" + number;
        if (!number.matches("\\+[1-9][0-9]{7,14}")
                || (number.startsWith("+254") && !number.matches("\\+254[17][0-9]{8}"))) {
            throw new IllegalArgumentException("Enter a Kenyan mobile number such as 0712345678 or 0112345678, or an international number starting with +");
        }
        return number;
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
    public PatientAssignmentDto createPatientAssignments(PatientAssignmentDto patientAssignmentDto) {
        PatientVisit  patientVisit = patientVisitRepository.findForUpdate(patientAssignmentDto.getPatientVisitId())
            .orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));

    PatientAssignment patientAssignment = PatientAssignment.builder()
            .patientVisit(patientVisit)
            .build();
        String userName = securityService.getCurrentUsername();
        User attendingOfficer = userService.findByUsername(userName);
        patientAssignment.setAttendingOfficer(attendingOfficer);
        Station nextStation = stationRepository.findByName(patientAssignmentDto.getNextStation()).
                orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAssignmentDto.getAssignedOfficer());
        validateAssignment(patientVisit, nextStation, assignedOfficer);
       var plan = patientAssignmentsRepo.findByAssignedOfficerAndNextStationAndPatientVisit(assignedOfficer,nextStation,patientVisit);
       if (plan.isPresent())
       {
           throw new EntityExistsException("You cannot assign the same station and officer to one patient visit.");

       }
        patientAssignment.setNextStation(nextStation);
        patientAssignment.setAssignedOfficer(assignedOfficer);
        patientVisit.getPatientAssignments().add(patientAssignment);
        patientVisitRepository.save(patientVisit);

        return patientAssignmentsMapper.toDto(patientAssignment);

    }

    @Transactional
    @Override
    public PatientAssignmentDto updatePatientAssignments(PatientAssignmentDto patientAssignmentDto) {
        Long visitId = patientAssignmentsRepo.findVisitId(patientAssignmentDto.getId()).orElseThrow(() -> new EntityNotFoundException("Patient assignment not found"));
        patientVisitRepository.findForUpdate(visitId).orElseThrow(() -> new EntityNotFoundException("Patient visit not found"));
       PatientAssignment patientAssignment = patientAssignmentsRepo.findById(patientAssignmentDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient attending plan not found"));
        if (patientAssignment.isDeleted() || patientAssignment.getAssignmentStatus() == Status.COMPLETED || patientAssignment.getAssignmentStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Completed or cancelled handoffs cannot be reassigned");
        }
        Station nextStation = stationRepository.findByName(patientAssignmentDto.getNextStation()).orElseThrow(()-> new EntityNotFoundException("Station not found"));
        User assignedOfficer = userService.findByUsername(patientAssignmentDto.getAssignedOfficer());
        validateAssignment(patientAssignment.getPatientVisit(), nextStation, assignedOfficer);
        patientAssignment.setNextStation(nextStation);
        patientAssignment.setAssignedOfficer(assignedOfficer);

        patientAssignment = patientAssignmentsRepo.save(patientAssignment);
        return patientAssignmentsMapper.toDto(patientAssignment);
    }

    private void validateAssignment(PatientVisit visit, Station station, User officer) {
        if (visit.isDeleted() || visit.getPatient().isDeleted()
                || visit.getVisitStatus() == Status.COMPLETED || visit.getVisitStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Only active visits can be handed off");
        }
        if (station.isDeleted() || officer == null || !officer.isEnabled() || officer.isDeleted() || !officer.isAvailable()) {
            throw new IllegalArgumentException("Assigned officer or station is not available");
        }
        boolean stationMember = officer.getStations().stream().anyMatch(member -> member.getId().equals(station.getId()));
        boolean allowedRole = officer.getRoles().stream().anyMatch(role -> station.getAllowedRoles().stream()
                .anyMatch(allowed -> allowed.getName().equals(role.getName())));
        if (!stationMember || !allowedRole) {
            throw new IllegalArgumentException("Select an available officer assigned to this station with an allowed role");
        }
    }


}
