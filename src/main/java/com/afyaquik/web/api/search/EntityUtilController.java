package com.afyaquik.web.api.search;

import com.afyaquik.utils.dto.delete.DeleteDto;
import com.afyaquik.utils.otherservices.EntityUtilService;
import com.afyaquik.utils.dto.search.SearchDto;
import com.afyaquik.utils.dto.search.SearchResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EntityUtilController {
    private final EntityUtilService entityUtilService;
    @PostMapping("/search")
    public ResponseEntity<SearchResponseDto> search(@RequestBody SearchDto searchDto) {


        SearchResponseDto response = entityUtilService.search(searchDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteDto deleteDto)
    {
        entityUtilService.softdelete(deleteDto.getEntityName(), deleteDto.getIds());
        return ResponseEntity.ok().build();
    }


}

