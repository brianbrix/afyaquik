package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.TreatmentPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    Page<TreatmentPlan> findAllByPatientVisitId(Long id, Pageable pageable);
}
