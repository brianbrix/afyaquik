package com.afyaquik.core.services;

import com.afyaquik.dtos.search.SearchDto;
import com.afyaquik.dtos.search.SearchResponseDto;
import org.springframework.data.domain.Pageable;

public interface SearchService {
     SearchResponseDto search(SearchDto searchDto, Pageable pageable);
}
