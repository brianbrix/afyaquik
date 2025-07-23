package com.afyaquik.doctor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObservationItemCategoryDto {
    private Long id;
    private String name;
    private String description;
}
