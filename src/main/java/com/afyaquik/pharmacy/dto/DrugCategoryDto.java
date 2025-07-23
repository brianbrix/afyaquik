package com.afyaquik.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrugCategoryDto {
    private Long id;
    @NotNull(message = "Category name cannot be null")
    @NotBlank(message = "Category name cannot be blank")
    private String name;
    private String description;
    private boolean enabled = true;
}
