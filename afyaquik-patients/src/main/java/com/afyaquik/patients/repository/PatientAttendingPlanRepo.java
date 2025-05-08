package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientAttendingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientAttendingPlanRepo extends JpaRepository<PatientAttendingPlan, Long>{
}
