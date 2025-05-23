package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientAttendingPlan;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientAttendingPlanRepo extends JpaRepository<PatientAttendingPlan, Long>{
    Page<PatientAttendingPlan> findAllByAssignedOfficerId(Long assignedOfficerId, Pageable pageable);
    Page<PatientAttendingPlan> findAllByAttendingOfficerId(Long attendingOfficerId, Pageable pageable);
    Page<PatientAttendingPlan> findAllByNextStationId(Long nextStationId, Pageable pageable);
    Page<PatientAttendingPlan> findAllByPatientVisitId(Long patientVisitId, Pageable pageable);

    Optional<PatientAttendingPlan> findByAssignedOfficerAndNextStationAndPatientVisit(User assignedOfficer, Station nextStation, PatientVisit patientVisit);
}
