package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.pharmacy.entity.DrugInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugInventoryRepository extends JpaRepository<DrugInventory, Long> {
    Optional<DrugInventory> findByBatchNumberAndBatchNumberNotNull(String batchNumber);
    List<DrugInventory> findByDrugIdAndActiveTrue(Long drugId);
    List<DrugInventory> findByDrugIdAndExpiryDateAfter(Long drugId, java.time.LocalDate expiryDate);
    List<DrugInventory> findByActiveTrue();
    Optional<DrugInventory> findByDrugAndBatchNumberAndActiveTrue(Drug drug, String batchNumber);

    boolean existsByDrugAndCurrentQuantityGreaterThanAndActiveTrue(Drug drug, double i);


    Page<DrugInventory> findAllByDrugAndCurrentQuantityGreaterThanAndActiveTrue(Drug drug, double i, Pageable pageable);

    Page<DrugInventory> findAllByActiveTrue(Pageable pageable);

    Page<DrugInventory> findAllByExpiryDateBeforeAndActiveTrue(LocalDate now, Pageable pageable);

    Page<DrugInventory> findAllByDrug(Drug drug, Pageable pageable);

    Page<DrugInventory> findAllByDrugAndActiveTrue(Drug drug, Pageable pageable);

    @Query("SELECT SUM(d.currentQuantity) FROM DrugInventory d WHERE d.drug.id = :id AND d.active = true")
    double sumCurrentQuantityByDrugIdAndActiveTrue(Long id);

    Optional<DrugInventory> findByBatchNumber(String batchNumber);
}

