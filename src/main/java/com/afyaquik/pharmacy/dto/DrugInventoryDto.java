package com.afyaquik.pharmacy.dto;


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
    private int currentQuantity;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private int initialQuantity;
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDateTime receivedDate;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private double sellingPrice;
    @PositiveOrZero(message = "Initial quantity cannot be less than zero")
    private double buyingPrice;
    private boolean isActive;
    private String drugName;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
