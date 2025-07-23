package com.afyaquik.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentDto {

    private Long id;

    private Long billingId;

    private BigDecimal amount;

    private String paymentMethod;

    private String paymentReference;

    private LocalDateTime paymentDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
