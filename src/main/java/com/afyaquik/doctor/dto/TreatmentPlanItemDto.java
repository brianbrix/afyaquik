package com.afyaquik.doctor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPlanItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean enabled;
}
