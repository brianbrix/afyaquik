package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.TreatmentPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentPlanItemRepository extends JpaRepository<TreatmentPlanItem, Long> {
}
