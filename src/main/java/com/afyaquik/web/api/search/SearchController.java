package com.afyaquik.web.api.search;

import com.afyaquik.utils.otherservices.SearchService;
import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @PostMapping
    public ResponseEntity<SearchResponseDto> search(@RequestBody SearchDto searchDto) {


        SearchResponseDto response = searchService.search(searchDto);
        return ResponseEntity.ok(response);
    }

}
