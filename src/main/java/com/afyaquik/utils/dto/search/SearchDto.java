package com.afyaquik.utils.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    private String query;
    private String searchEntity;
    @Builder.Default
    private List<String> searchFields = new ArrayList<>();
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;
    private String dateFilter;
    private String sort;
}
