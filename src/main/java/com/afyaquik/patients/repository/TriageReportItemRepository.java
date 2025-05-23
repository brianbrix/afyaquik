package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.TriageReport;
import com.afyaquik.patients.entity.TriageReportItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriageReportItemRepository extends JpaRepository<TriageReportItem, Long> {
    Page<TriageReportItem> findAllByTriageReport(Pageable pageable,TriageReport triageReport);
}
