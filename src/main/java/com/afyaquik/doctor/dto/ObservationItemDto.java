package com.afyaquik.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObservationItemDto {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Category is required")
    private Long category;
    private String categoryName;
    private Double price;
    @NotNull(message = "Station is required")
    private Long station;
    private String stationName;
    private boolean mandatory;
}
