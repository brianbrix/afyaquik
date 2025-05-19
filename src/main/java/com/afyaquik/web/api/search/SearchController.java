package com.afyaquik.web.api.search;

import com.afyaquik.utils.services.SearchService;
import com.afyaquik.dtos.search.SearchDto;
import com.afyaquik.dtos.search.SearchResponseDto;
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

        Pageable pageable;
        if (searchDto.getSort() != null && !searchDto.getSort().isBlank()) {
            String[] sortParts = searchDto.getSort().split(",");
            if (sortParts.length == 2) {
                String sortField = sortParts[0].trim();
                String sortDirection = sortParts[1].trim().toLowerCase();
                Sort sort = sortDirection.equals("desc")
                        ? Sort.by(Sort.Order.desc(sortField))
                        : Sort.by(Sort.Order.asc(sortField));

                pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
            } else {
                pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
            }
        } else {
            pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
        }

        SearchResponseDto response = searchService.search(searchDto, pageable);
        return ResponseEntity.ok(response);
    }

}
