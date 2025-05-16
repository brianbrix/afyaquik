package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientAttendingPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientAttendingPlanRepo extends JpaRepository<PatientAttendingPlan, Long>{
    Page<PatientAttendingPlan> findAllByAssignedOfficerId(Long assignedOfficerId, org.springframework.data.domain.Pageable pageable);
    Page<PatientAttendingPlan> findAllByAttendingOfficerId(Long attendingOfficerId, org.springframework.data.domain.Pageable pageable);
    Page<PatientAttendingPlan> findAllByNextStationId(Long nextStationId, org.springframework.data.domain.Pageable pageable);
    Page<PatientAttendingPlan> findAllByPatientVisitId(Long patientVisitId, Pageable pageable);
}
