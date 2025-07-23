package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.BillingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingItemRepository extends JpaRepository<BillingItem, Long> {

    List<BillingItem> findByActiveTrue();

    List<BillingItem> findByNameContainingIgnoreCase(String name);

    // Find a billing item by exact name (case insensitive)
    Optional<BillingItem> findByNameIgnoreCase(String name);
}
