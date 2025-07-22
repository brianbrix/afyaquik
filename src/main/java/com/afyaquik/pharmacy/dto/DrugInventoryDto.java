package com.afyaquik.pharmacy.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DrugInventoryDto {
    private Long id;
    private Long drugId;
    private double currentQuantity;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private double initialQuantity;
    private String batchNumber;
    private String comment;
    private boolean locked;
    @NotNull(message = "Expiry date must be provided")
    private LocalDate expiryDate;
    private LocalDateTime receivedDate;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private double sellingPrice;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private double buyingPrice;
    private boolean isActive;
    private String drugName;
    private String supplierName;
    private boolean usePriceAsCurrentForDrug=true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
