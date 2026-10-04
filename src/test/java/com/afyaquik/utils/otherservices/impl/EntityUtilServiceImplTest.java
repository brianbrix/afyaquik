package com.afyaquik.utils.otherservices.impl;

import com.afyaquik.utils.mappers.MapperRegistry;
import com.afyaquik.utils.dto.search.SearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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
                authenticateAs("ADMIN");
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

        @AfterEach
        void clearSecurityContext() {
                SecurityContextHolder.clearContext();
        }

        private void authenticateAs(String role) {
                SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken("staff", null,
                                                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
        }

        @Test
        void search_WithoutAuthentication_ShouldDenyAccess() {
                SecurityContextHolder.clearContext();
                assertThrows(AccessDeniedException.class, () -> searchService.search(searchDto));
                verifyNoInteractions(mapperRegistry);
        }

        @Test
        void search_ReceptionistCannotReadUserAdministration() {
                authenticateAs("RECEPTIONIST");
                assertThrows(AccessDeniedException.class, () -> searchService.search(searchDto));
                verifyNoInteractions(mapperRegistry);
        }

        @Test
        void search_DoctorCanReadPatientsWithPrefixedAuthority() {
                authenticateAs("DOCTOR");
                searchDto.setSearchEntity("patients");
                assertThrows(IllegalArgumentException.class, () -> searchService.search(searchDto));
                verify(mapperRegistry).getMapper("patients");
        }

        @Test
        void search_CashierCannotReadClinicalPlans() {
                authenticateAs("CASHIER");
                searchDto.setSearchEntity("treatmentPlans");
                assertThrows(AccessDeniedException.class, () -> searchService.search(searchDto));
                verifyNoInteractions(mapperRegistry);
        }

        @Test
        void softdelete_ReceptionistCannotDeletePatients() {
                authenticateAs("RECEPTIONIST");
                assertThrows(AccessDeniedException.class, () -> searchService.softdelete("patients", List.of(1L)));
        }

        @Test
        void softdelete_AdminCannotDeleteRoles() {
                assertThrows(AccessDeniedException.class, () -> searchService.softdelete("roles", List.of(1L)));
        }

    @Test
        void genericDeletionCannotBypassStaffPrivilegeChecks() {
                assertThrows(AccessDeniedException.class, () -> searchService.softdelete("users", List.of(1L)));
                authenticateAs("SUPERADMIN");
                assertThrows(AccessDeniedException.class, () -> searchService.softdelete("users", List.of(1L)));
        }

        @Test
        void unboundedSearchIsRejectedBeforeQuerying() {
                searchDto.setSize(10000);
                assertThrows(IllegalArgumentException.class, () -> searchService.search(searchDto));
                verifyNoInteractions(mapperRegistry);
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
        assertEquals("Unknown search entity: invalidEntity", exception.getMessage());
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
