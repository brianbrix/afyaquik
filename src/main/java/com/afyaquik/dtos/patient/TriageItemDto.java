package com.afyaquik.dtos.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageItemDto {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
}
