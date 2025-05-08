package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.department.DepartmentDto;
import com.afyaquik.users.entity.Department;
import com.afyaquik.users.repository.DepartmentRepository;
import com.afyaquik.users.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department  department = new Department();
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());
        departmentRepository.save(department);
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(()-> new EntityNotFoundException("Department not found."));
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());
        departmentRepository.save(department);
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }

    @Override
    public void deleteDepartment(Long departmentId) {
        departmentRepository.findById(departmentId).orElseThrow(()-> new EntityNotFoundException("Department not found."));
        departmentRepository.deleteById(departmentId);
    }

    @Override
    public DepartmentDto getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId).map(department -> DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build()).orElseThrow(()-> new EntityNotFoundException("Department not found."));
    }

    @Override
    public List<DepartmentDto> getDepartments() {
        return departmentRepository.findAll().stream().map(department -> DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build()).toList();
    }
}
