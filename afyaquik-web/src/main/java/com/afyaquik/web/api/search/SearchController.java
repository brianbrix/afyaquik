package com.afyaquik.web.api.search;

import com.afyaquik.core.settings.services.SearchService;
import com.afyaquik.dtos.search.SearchDto;
import com.afyaquik.dtos.search.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @PostMapping
    public ResponseEntity<SearchResponseDto> search(
            @RequestBody SearchDto searchDto) {

        Pageable pageable = searchDto.getSort() != null ?
                PageRequest.of(searchDto.getPage(), searchDto.getSize(), Sort.by(searchDto.getSort())) :
                PageRequest.of(searchDto.getPage(), searchDto.getSize());

        SearchResponseDto response = searchService.search(searchDto, pageable);
        return ResponseEntity.ok(response);
    }

}
