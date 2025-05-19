package com.afyaquik.dtos.search;

import lombok.*;
import org.springframework.data.domain.Page;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ListFetchDto <T>{
    private Page<T> results;
}
