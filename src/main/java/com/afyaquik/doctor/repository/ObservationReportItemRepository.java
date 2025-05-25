package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.ObservationReportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationReportItemRepository extends JpaRepository<ObservationReportItem, Long>{
}
