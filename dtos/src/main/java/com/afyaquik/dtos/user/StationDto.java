package com.afyaquik.dtos.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationDto {
    private Long id;
    private String name;
}
