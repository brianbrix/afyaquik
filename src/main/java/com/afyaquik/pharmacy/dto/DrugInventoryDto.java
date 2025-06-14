package com.afyaquik.pharmacy.dto;

import com.afyaquik.pharmacy.entity.Drug;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private int initialQuantity;
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDateTime receivedDate;
    private double price;
    private boolean isActive;
    private String drugName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
