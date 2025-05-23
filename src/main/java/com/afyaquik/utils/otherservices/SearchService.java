package com.afyaquik.utils.otherservices;

import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;
import org.springframework.data.domain.Pageable;

public interface SearchService {
     SearchResponseDto search(SearchDto searchDto, Pageable pageable);
}
