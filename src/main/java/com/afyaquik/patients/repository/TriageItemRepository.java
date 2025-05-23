package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.TriageItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TriageItemRepository extends JpaRepository<TriageItem, Long>{
    Optional<TriageItem> findByName(String name);
}
