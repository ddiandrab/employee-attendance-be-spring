package com.employeeapp.employeeservice.department.mapper;

import com.employeeapp.employeeservice.department.dto.DepartmentResponse;
import com.employeeapp.employeeservice.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt().toLocalDateTime())
                .updatedAt(department.getUpdatedAt().toLocalDateTime())
                .build();
    }
}