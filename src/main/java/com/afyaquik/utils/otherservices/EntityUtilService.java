package com.afyaquik.utils.otherservices;

import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;

import java.util.List;

public interface EntityUtilService {
     SearchResponseDto search(SearchDto searchDto);
     void softdelete(String entityName, List<Long> ids);
}
