package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.Billing;
import com.afyaquik.billing.entity.BillingDetail;
import com.afyaquik.billing.entity.BillingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {

    @Query("select detail.billing.id from BillingDetail detail where detail.id = :id")
    Optional<Long> findBillingId(@Param("id") Long id);

    List<BillingDetail> findByBilling(Billing billing);

    List<BillingDetail> findByBillingId(Long billingId);

    List<BillingDetail> findByBillingItem(BillingItem billingItem);

    List<BillingDetail> findByBillingItemId(Long billingItemId);
}
