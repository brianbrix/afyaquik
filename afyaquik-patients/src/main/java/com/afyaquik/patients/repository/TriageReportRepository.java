package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.TriageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriageReportRepository extends JpaRepository<TriageReport, Long>{
}
