package com.afyaquik.users.dto;

import com.afyaquik.users.dto.department.DepartmentDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDto {

    private Long id;

    private UserDto user;

    private String specialty;


    private DepartmentDto department;
}
