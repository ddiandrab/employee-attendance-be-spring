package com.employeeapp.employeeservice.employee.mapper;

import com.employeeapp.employeeservice.employee.dto.EmployeeResponse;
import com.employeeapp.employeeservice.employee.entity.Employee;

import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .photoUrl(employee.getPhotoUrl())
                .position(employee.getPosition())
                .joinDate(employee.getJoinDate())
                .isActive(employee.getIsActive())
                .userId(employee.getUser().getId())
                .departmentId(employee.getDepartment() != null
                                ? employee.getDepartment().getId()
                                : null)
                .departmentName(employee.getDepartment() != null
                                ? employee.getDepartment().getName()
                                : null)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}