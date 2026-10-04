package com.afyaquik.billing.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillPayment extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentReference;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private String notes;
    private String currencyCode;
    private String patientNameSnapshot;
    private String facilityNameSnapshot;
    private String receivedBy;
    private BigDecimal billTotalSnapshot;
    private BigDecimal paidToDateSnapshot;
    private BigDecimal balanceSnapshot;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reversed;
    private String reversalReason;
    private String reversedBy;
    private LocalDateTime reversedAt;
}
