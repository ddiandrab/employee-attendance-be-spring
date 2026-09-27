package com.employeeapp.employeeservice.employee.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class EmployeeResponse {

    private Integer id;

    private String employeeNumber;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String photoUrl;

    private String position;

    private LocalDate joinDate;

    private Boolean isActive;

    private Integer userId;

    private Integer departmentId;

    private String departmentName;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}