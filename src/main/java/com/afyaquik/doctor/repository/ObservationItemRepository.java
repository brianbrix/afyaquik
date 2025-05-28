package com.afyaquik.doctor.repository;

import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.doctor.enums.ObservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObservationItemRepository extends JpaRepository<ObservationItem, Long>{
    Optional<ObservationItem> findByNameAndCategoryId(String name, Long categoryId);
    @Query("SELECT o FROM ObservationItem o")
    Page<ObservationItem>getAllPaginated(Pageable pageable);
}
