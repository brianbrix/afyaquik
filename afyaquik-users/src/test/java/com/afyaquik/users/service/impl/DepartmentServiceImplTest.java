package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.department.DepartmentDto;
import com.afyaquik.users.entity.Department;
import com.afyaquik.users.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    /**
     * Test case for creating a new department successfully.
     * This test verifies that the createDepartment method correctly creates a new department
     * and returns the corresponding DepartmentDto with the expected values.
     */
    @Test
    public void testCreateDepartmentSuccessfully() {
        DepartmentDto inputDto = DepartmentDto.builder()
                .name("Test Department")
                .description("Test Description")
                .build();

        Department savedDepartment = new Department();
        savedDepartment.setId(1L);
        savedDepartment.setName("Test Department");
        savedDepartment.setDescription("Test Description");

        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        DepartmentDto result = departmentService.createDepartment(inputDto);

        assertEquals(1L, result.getId());
        assertEquals("Test Department", result.getName());
        assertEquals("Test Description", result.getDescription());

        Mockito.verify(departmentRepository).save(any(Department.class));
    }

    /**
     * Test case for deleteDepartment method when the department is not found.
     * It verifies that an EntityNotFoundException is thrown when trying to delete a non-existent department.
     */
    @Test
    public void testDeleteDepartmentWhenDepartmentNotFound() {
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departmentService.deleteDepartment(departmentId));

        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository, never()).deleteById(anyLong());
    }

    /**
     * Test the deleteDepartment method when the department is not found.
     * This test verifies that an EntityNotFoundException is thrown when
     * trying to delete a non-existent department.
     */
    @Test
    public void testDeleteDepartment_DepartmentNotFound() {
        Long nonExistentDepartmentId = 1L;
        when(departmentRepository.findById(nonExistentDepartmentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            departmentService.deleteDepartment(nonExistentDepartmentId);
        });
    }

    /**
     * Test case for getDepartment method when the department exists.
     * It verifies that the method correctly retrieves and maps a department to a DepartmentDto.
     */
    @Test
    public void testGetDepartmentWhenDepartmentExists() {
        Long departmentId = 1L;
        Department department = new Department();
        department.setId(departmentId);
        department.setName("IT");
        department.setDescription("Information Technology");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        DepartmentDto result = departmentService.getDepartment(departmentId);

        assertNotNull(result);
        assertEquals(departmentId, result.getId());
        assertEquals("IT", result.getName());
        assertEquals("Information Technology", result.getDescription());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    /**
     * Test getDepartment method when the department is not found.
     * This test verifies that an EntityNotFoundException is thrown when
     * attempting to retrieve a non-existent department.
     */
    @Test
    public void testGetDepartment_NonExistentDepartment_ThrowsEntityNotFoundException() {
        Long nonExistentDepartmentId = 999L;
        when(departmentRepository.findById(nonExistentDepartmentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            departmentService.getDepartment(nonExistentDepartmentId);
        });
    }

    /**
     * Test case for updating an existing department.
     * This test verifies that the updateDepartment method correctly updates
     * the name and description of an existing department and returns the
     * updated department details as a DepartmentDto.
     */
    @Test
    public void testUpdateExistingDepartment() {
        Long departmentId = 1L;
        DepartmentDto inputDto = new DepartmentDto();
        inputDto.setName("Updated Department");
        inputDto.setDescription("Updated Description");

        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("Old Department");
        existingDepartment.setDescription("Old Description");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(existingDepartment);

        DepartmentDto result = departmentService.updateDepartment(departmentId, inputDto);

        assertNotNull(result);
        assertEquals(departmentId, result.getId());
        assertEquals("Updated Department", result.getName());
        assertEquals("Updated Description", result.getDescription());

        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository).save(existingDepartment);
    }

    /**
     * Test case for getDepartments method.
     * It verifies that the method correctly retrieves all departments from the repository,
     * transforms them into DTOs, and returns the list of DTOs.
     */
    @Test
    public void test_getDepartments_returnsListOfDepartmentDtos() {
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("HR");
        dept1.setDescription("Human Resources");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setName("IT");
        dept2.setDescription("Information Technology");

        List<Department> departments = Arrays.asList(dept1, dept2);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<DepartmentDto> result = departmentService.getDepartments();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("HR", result.get(0).getName());
        assertEquals("Human Resources", result.get(0).getDescription());
        assertEquals(2L, result.get(1).getId());
        assertEquals("IT", result.get(1).getName());
        assertEquals("Information Technology", result.get(1).getDescription());
    }

    /**
     * Tests the updateDepartment method when the department with the given ID is not found.
     * Expects an EntityNotFoundException to be thrown.
     */
    @Test
    public void test_updateDepartment_departmentNotFound() {
        Long nonExistentDepartmentId = 999L;
        DepartmentDto departmentDto = new DepartmentDto();

        when(departmentRepository.findById(nonExistentDepartmentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            departmentService.updateDepartment(nonExistentDepartmentId, departmentDto);
        });
    }

}
