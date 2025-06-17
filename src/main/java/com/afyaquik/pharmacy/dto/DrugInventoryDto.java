package com.afyaquik.pharmacy.dto;


import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NegativeOrZero(message = "Initial quantity cannot be negative or zero")
    private int initialQuantity;
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDateTime receivedDate;
    @NegativeOrZero(message = "Initial quantity cannot be negative or zero")
    private double sellingPrice;
    @NegativeOrZero(message = "Initial quantity cannot be negative or zero")
    private double buyingPrice;
    private boolean isActive;
    private String drugName;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
