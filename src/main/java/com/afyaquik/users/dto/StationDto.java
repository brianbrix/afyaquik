package com.afyaquik.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {
    private Long id;
    private String name;
    @Builder.Default
    private Set<String> allowedRoles = new HashSet<>();
}
