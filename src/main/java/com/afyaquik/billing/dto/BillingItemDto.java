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
public class BillingItemDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal defaultAmount;

    private boolean active;

    private Long currencyId;

    private String currencyCode;

    private String currencySymbol;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
