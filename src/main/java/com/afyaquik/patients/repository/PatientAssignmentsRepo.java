package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientAssignment;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientAssignmentsRepo extends JpaRepository<PatientAssignment, Long>{
    Page<PatientAssignment> findAllByAssignedOfficerId(Long assignedOfficerId, Pageable pageable);
    Page<PatientAssignment> findAllByAttendingOfficerId(Long attendingOfficerId, Pageable pageable);
    Page<PatientAssignment> findAllByNextStationId(Long nextStationId, Pageable pageable);
    Page<PatientAssignment> findAllByPatientVisitId(Long patientVisitId, Pageable pageable);
    Page<PatientAssignment> findAllByPatientVisitIdAndAssignedOfficerId(Long patientVisitId, Long assignedOfficerId, Pageable pageable);
    Page<PatientAssignment> findAllByPatientVisitIdAndAttendingOfficerId(Long patientVisitId, Long attendingOfficerId, Pageable pageable);

    Optional<PatientAssignment> findByAssignedOfficerAndNextStationAndPatientVisit(User assignedOfficer, Station nextStation, PatientVisit patientVisit);
}
