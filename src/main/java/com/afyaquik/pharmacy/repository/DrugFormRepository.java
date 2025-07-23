package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.DrugForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugFormRepository extends JpaRepository<DrugForm, Long> {
    Optional<DrugForm> findByNameIgnoreCase(String name);
}
