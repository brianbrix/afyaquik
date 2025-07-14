package com.afyaquik.billing.dto;

import com.afyaquik.patients.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDto {

    private Long id;

    private Long patientVisitId;

    private String patientName;

    private BigDecimal amount;

    private BigDecimal discount;

    private BigDecimal totalAmount;

    private String description;

    private Status status;

    private LocalDateTime paidAt;

    private String paymentMethod;

    private String paymentReference;

    private BigDecimal amountDue;

    private List<BillingDetailDto> billingDetails = new ArrayList<>();

    private List<BillPaymentDto> payments = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
