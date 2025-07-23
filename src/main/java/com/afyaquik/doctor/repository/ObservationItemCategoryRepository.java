package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.ObservationItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationItemCategoryRepository extends JpaRepository<ObservationItemCategory, Long> {
}
