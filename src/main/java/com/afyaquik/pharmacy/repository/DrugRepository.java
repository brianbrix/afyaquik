package com.afyaquik.pharmacy.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.afyaquik.pharmacy.entity.Drug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {
    Optional<Drug> findByName(String name);
    Optional<Drug> findByNameAndBrandName(String name, String brandName);
    Optional<Drug> findByAtcCode(String code);

    Page<Drug> findAllByEnabled(Pageable pageable, boolean enabled);
}
