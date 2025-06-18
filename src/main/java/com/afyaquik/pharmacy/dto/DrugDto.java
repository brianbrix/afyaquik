package com.afyaquik.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DrugDto {
    private Long id;
    @NotBlank(message = "Drug name cannot be blank")
    @NotNull(message = "Drug name cannot be null")
    private String name;
    private String brandName;
    private String description;
    private String categoryName;
    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;
    private Long drugFormId;
    private String drugFormName;
    private String strength;
    private String manufacturer;
    private String sampleDosageInstruction;
    private int stockQuantity;//total of all current quantities in inventory for this drug
    private boolean isPrescriptionRequired=false;
    private String atcCode;
    private boolean enabled=true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
