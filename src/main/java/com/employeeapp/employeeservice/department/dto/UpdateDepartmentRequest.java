package com.employeeapp.employeeservice.department.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDepartmentRequest {

    @Size(
        min = 1,
        max = 100,
        message = "Name must be between 1 and 100 characters"
    )
    private String name;

    @Size(
        max = 255,
        message = "Description must not exceed 255 characters"
    )
    private String description;
}