package com.afyaquik.billing.entity;

import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_visit_id", nullable = false)
    private PatientVisit patientVisit;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal discount;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String description;
    private String currencyCode;
    private BigDecimal openingAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    private LocalDateTime paidAt;

    private String paymentMethod;

    private String paymentReference;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillingDetail> billingDetails = new ArrayList<>();

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillPayment> payments = new ArrayList<>();

    // Calculated field for convenience
    public BigDecimal getAmountDue() {
        // Calculate total paid amount
        if (payments==null)
        {
            payments = new ArrayList<>();
        }
        BigDecimal totalPaid = payments.stream()
            .filter(payment -> !payment.isReversed() && !payment.isDeleted())
                .map(BillPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Return total amount minus total paid
        return totalAmount.subtract(totalPaid);
    }

    // Helper method to add a payment
    public void addPayment(BillPayment payment) {
        if (payment == null || payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (payments == null) payments = new ArrayList<>();
        payments.add(payment);
        payment.setBilling(this);

        // If payment covers the full amount due, mark as PAID
        if (getAmountDue().compareTo(BigDecimal.ZERO) <= 0) {
            this.status = Status.PAID;
            this.paidAt = LocalDateTime.now();
        }
    }

    // Helper method to remove a payment
    public void removePayment(BillPayment payment) {
        payments.remove(payment);
        payment.setBilling(null);

        // If there are no payments or amount due is greater than zero, mark as PENDING
        if (payments.isEmpty() || getAmountDue().compareTo(BigDecimal.ZERO) > 0) {
            this.status = Status.PENDING;
            this.paidAt = null;
        }
    }

    public void refreshPaymentStatus() {
        if (status == Status.CANCELLED) return;
        status = getAmountDue().signum() <= 0 ? Status.PAID : Status.PENDING;
        paidAt = status == Status.PAID ? LocalDateTime.now() : null;
    }

    // Helper method to add a billing detail
    public void addBillingDetail(BillingDetail billingDetail) {
        if (billingDetails == null)
            billingDetails =new ArrayList<>();
        billingDetails.add(billingDetail);
        billingDetail.setBilling(this);
    }

    // Helper method to remove a billing detail
    public void removeBillingDetail(BillingDetail billingDetail) {
        if (billingDetails == null)
            billingDetails =new ArrayList<>();
        billingDetails.remove(billingDetail);
        billingDetail.setBilling(null);
    }
}
