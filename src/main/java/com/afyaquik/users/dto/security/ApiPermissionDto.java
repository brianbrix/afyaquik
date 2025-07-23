package com.afyaquik.users.dto.security;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ApiPermissionDto {

        private Long id;
        private String urlPattern;
        private String description;
        private boolean enabled = true;
        private Set<String> roleNames;



}
