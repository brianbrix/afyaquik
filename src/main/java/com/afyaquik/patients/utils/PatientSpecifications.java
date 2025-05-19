package com.afyaquik.patients.utils;

import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.entity.Patient;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecifications {
    public static Specification<Patient> filterByPatientDto(PatientDto dto) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (dto.getId() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("id"), dto.getId()));
            }
            if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + dto.getFirstName().toLowerCase() + "%"));
            }
            if (dto.getSecondName() != null && !dto.getSecondName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("secondName")), "%" + dto.getSecondName().toLowerCase() + "%"));
            }
            if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + dto.getLastName().toLowerCase() + "%"));
            }
            if (dto.getGender() != null && !dto.getGender().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("gender"), dto.getGender()));
            }
            if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("dateOfBirth"), dto.getDateOfBirth()));
            }
            if (dto.getNationalId() != null && !dto.getNationalId().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("nationalId"), dto.getNationalId()));
            }
            if (dto.getMaritalStatus() != null && !dto.getMaritalStatus().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("maritalStatus"), dto.getMaritalStatus()));
            }

            if (dto.getContactInfo() != null) {
                Join<Object, Object> contactJoin = root.join("contactInfo", JoinType.LEFT);

                if (dto.getContactInfo().getPhone() != null && !dto.getContactInfo().getPhone().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(contactJoin.get("phoneNumber"), "%" + dto.getContactInfo().getPhone() + "%"));
                }
                if (dto.getContactInfo().getEmail() != null && !dto.getContactInfo().getEmail().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(criteriaBuilder.lower(contactJoin.get("email")), "%" + dto.getContactInfo().getEmail().toLowerCase() + "%"));
                }
           }

            return predicate;
        };
    }
}
