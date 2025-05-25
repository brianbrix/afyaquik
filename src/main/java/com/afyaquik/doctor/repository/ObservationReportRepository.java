package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.ObservationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationReportRepository extends JpaRepository<ObservationReport, Long>{
    @Query("SELECT o FROM ObservationReport o WHERE o.patientVisit.patient.id = :patientId")
    Page<ObservationReport> findByPatientId(Long patientId, Pageable pageable);
}
