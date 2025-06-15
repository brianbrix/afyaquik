package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.pharmacy.entity.DrugInventory;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugInventoryRepository extends JpaRepository<DrugInventory, Long> {
    Optional<DrugInventory> findByBatchNumberAndBatchNumberNotNull(String batchNumber);
    List<DrugInventory> findByDrugId(Long drugId);
    List<DrugInventory> findByDrugIdAndExpiryDateAfter(Long drugId, java.time.LocalDate expiryDate);
    List<DrugInventory> findByActiveTrue();
    Optional<DrugInventory> findByDrugAndBatchNumberAndActiveTrue(Drug drug, String batchNumber);

    boolean existsByDrugAndCurrentQuantityGreaterThan(Drug drug, int i);

    Optional<DrugInventory> findByDrugAndBatchNumber(Drug drug, String batchNumber);

    Page<DrugInventory> findAllByDrugAndCurrentQuantityGreaterThan(Drug drug, int i, Pageable pageable);

    Page<DrugInventory> findAllByActiveTrue(Pageable pageable);

    Page<DrugInventory> findAllByExpiryDateBeforeAndActiveTrue(LocalDate now, Pageable pageable);

    Page<DrugInventory> findAllByDrug(Drug drug, Pageable pageable);

    Page<DrugInventory> findAllByDrugAndActiveTrue(Drug drug, Pageable pageable);
}

