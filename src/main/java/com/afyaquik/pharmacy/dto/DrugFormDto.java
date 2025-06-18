package com.afyaquik.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrugFormDto {
    private Long id;
    private String name;
    private String description;
}
