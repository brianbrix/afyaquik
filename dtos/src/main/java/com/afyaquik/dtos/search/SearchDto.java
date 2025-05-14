package com.afyaquik.dtos.search;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@Data
public class SearchDto {
    private String query;
    private String searchEntity;
    private List<String> searchFields;
    private int page=0;
    private int size=10;
    private String sort;

}
