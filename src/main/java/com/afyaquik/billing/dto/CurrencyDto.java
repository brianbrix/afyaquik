package com.afyaquik.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    private Long id;

    private String name;

    private String code;

    private String symbol;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
