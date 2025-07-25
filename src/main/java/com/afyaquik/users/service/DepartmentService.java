package com.afyaquik.users.service;

import com.afyaquik.users.dto.department.DepartmentDto;

public interface DepartmentService {
    DepartmentDto  createDepartment(DepartmentDto departmentDto);
    DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto);
    void deleteDepartment(Long departmentId);
    DepartmentDto getDepartment(Long departmentId);

    Object getDepartments();
}
