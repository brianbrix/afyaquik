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
public class BillingDetailDto {

    private Long id;

    private Long billingId;

    private Long billingItemId;

    private String billingItemName;

    private BigDecimal amount;

    private String description;

    private Integer quantity;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
