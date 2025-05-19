package com.afyaquik.dtos.user;

import com.afyaquik.dtos.user.department.DepartmentDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NurseDto {

    private Long id;

    private UserDto user;

    private String specialty;


    private DepartmentDto department;
}
