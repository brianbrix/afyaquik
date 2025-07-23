package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.Billing;
import com.afyaquik.billing.entity.BillingDetail;
import com.afyaquik.billing.entity.BillingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {

    List<BillingDetail> findByBilling(Billing billing);

    List<BillingDetail> findByBillingId(Long billingId);

    List<BillingDetail> findByBillingItem(BillingItem billingItem);

    List<BillingDetail> findByBillingItemId(Long billingItemId);
}
