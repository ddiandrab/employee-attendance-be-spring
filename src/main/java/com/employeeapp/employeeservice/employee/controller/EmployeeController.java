package com.employeeapp.employeeservice.employee.controller;

import com.employeeapp.employeeservice.employee.dto.CreateEmployeeRequest;
import com.employeeapp.employeeservice.employee.dto.EmployeeResponse;
import com.employeeapp.employeeservice.employee.dto.UpdateMyProfileRequest;
import com.employeeapp.employeeservice.employee.service.EmployeeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(
            @Valid
            @RequestBody
            CreateEmployeeRequest request
    ) {

        return employeeService.create(request);
    }

    @PatchMapping("/me")
    public EmployeeResponse updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateMyProfileRequest request) {
        return employeeService.updateMyProfile(authentication.getName(), request);
    }
}
