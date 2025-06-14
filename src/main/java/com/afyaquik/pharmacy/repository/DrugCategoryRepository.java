package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugCategoryRepository extends JpaRepository<DrugCategory, Long> {
}
