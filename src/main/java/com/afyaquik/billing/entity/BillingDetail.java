package com.afyaquik.billing.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDetail extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_item_id", nullable = false)
    private BillingItem billingItem;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    private Integer quantity = 1;

    @Column(nullable = false)
    private BigDecimal totalAmount;
}
