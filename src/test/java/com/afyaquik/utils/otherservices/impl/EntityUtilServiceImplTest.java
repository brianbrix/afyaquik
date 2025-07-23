package com.afyaquik.utils.otherservices.impl;

import com.afyaquik.utils.mappers.MapperRegistry;
import com.afyaquik.utils.dto.search.SearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SearchServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class EntityUtilServiceImplTest {

    @Mock
    private MapperRegistry mapperRegistry;

    @InjectMocks
    private EntityUtilServiceImpl searchService;

    private SearchDto searchDto;

    @BeforeEach
    void setUp() {
        // Set up test data
        List<String> searchFields = new ArrayList<>();
        searchFields.add("firstName");
        searchFields.add("lastName");

        searchDto = SearchDto.builder()
                .searchEntity("users")
                .page(0)
                .size(10)
                .query("test")
                .searchFields(searchFields)
                .build();
    }

    @Test
    void search_WithNullSearchEntity_ShouldThrowIllegalArgumentException() {
        // Given
        SearchDto testDto = SearchDto.builder()
                .searchEntity(null)
                .page(0)
                .size(10)
                .query("test")
                .searchFields(searchDto.getSearchFields())
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> searchService.search(testDto));
    }

    @Test
    void search_WithNoMapperForEntity_ShouldThrowIllegalArgumentException() {
        // Given
        SearchDto testDto = SearchDto.builder()
                .searchEntity("users") // Use a valid entity key that exists in resolveEntityClass
                .page(0)
                .size(10)
                .query("test")
                .searchFields(searchDto.getSearchFields())
                .build();

        when(mapperRegistry.getMapper("users")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.search(testDto));
        assertEquals("No mapper registered for entity: users", exception.getMessage());
        verify(mapperRegistry).getMapper("users");
    }

    @Test
    void search_WithInvalidSearchEntity_ShouldThrowIllegalArgumentException() {
        // Given
        SearchDto testDto = SearchDto.builder()
                .searchEntity("invalidEntity")
                .page(0)
                .size(10)
                .query("test")
                .searchFields(searchDto.getSearchFields())
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.search(testDto));
        assertTrue(exception.getMessage().contains("Entity class not found for: invalidEntity"));
    }

    @Test
    void search_WithInvalidDateFilter_ShouldThrowIllegalArgumentException() {
        // Given
        SearchDto testDto = SearchDto.builder()
                .searchEntity("users")
                .page(0)
                .size(10)
                .query("test")
                .searchFields(searchDto.getSearchFields())
                .dateFilter("invalidDateFilter")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> searchService.search(testDto));
    }
}
