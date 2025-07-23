package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.DrugCategory;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugCategoryRepository extends JpaRepository<DrugCategory, Long> {

    Optional<DrugCategory> findByNameIgnoreCase(@NotNull(message = "Category name cannot be null") @NotBlank(message = "Category name cannot be blank") String name);
}
