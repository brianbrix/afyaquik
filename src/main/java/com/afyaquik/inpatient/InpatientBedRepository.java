package com.afyaquik.inpatient;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface InpatientBedRepository extends JpaRepository<InpatientBed, Long> {
    List<InpatientBed> findByDeletedFalseOrderByWardAscCodeAsc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bed from InpatientBed bed where bed.id = :id")
    Optional<InpatientBed> findForUpdate(@Param("id") Long id);
}