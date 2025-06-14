package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.DrugInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugInventoryRepository extends JpaRepository<DrugInventory, Long> {
    Optional<DrugInventory> findByBatchNumber(String batchNumber);
    List<DrugInventory> findByDrugId(Long drugId);
    List<DrugInventory> findByDrugIdAndExpiryDateAfter(Long drugId, java.time.LocalDate expiryDate);
    List<DrugInventory> findByActiveTrue();
}

