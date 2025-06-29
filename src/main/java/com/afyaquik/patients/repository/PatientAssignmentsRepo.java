package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientAssignments;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientAssignmentsRepo extends JpaRepository<PatientAssignments, Long>{
    Page<PatientAssignments> findAllByAssignedOfficerId(Long assignedOfficerId, Pageable pageable);
    Page<PatientAssignments> findAllByAttendingOfficerId(Long attendingOfficerId, Pageable pageable);
    Page<PatientAssignments> findAllByNextStationId(Long nextStationId, Pageable pageable);
    Page<PatientAssignments> findAllByPatientVisitId(Long patientVisitId, Pageable pageable);
    Page<PatientAssignments> findAllByPatientVisitIdAndAssignedOfficerId(Long patientVisitId,Long assignedOfficerId,Pageable pageable);
    Page<PatientAssignments> findAllByPatientVisitIdAndAttendingOfficerId(Long patientVisitId, Long attendingOfficerId, Pageable pageable);

    Optional<PatientAssignments> findByAssignedOfficerAndNextStationAndPatientVisit(User assignedOfficer, Station nextStation, PatientVisit patientVisit);
}
