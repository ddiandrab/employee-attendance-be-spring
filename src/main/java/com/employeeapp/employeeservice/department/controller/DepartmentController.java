package com.employeeapp.employeeservice.department.controller;

import com.employeeapp.employeeservice.department.dto.CreateDepartmentRequest;
import com.employeeapp.employeeservice.department.dto.DepartmentResponse;
import com.employeeapp.employeeservice.department.dto.UpdateDepartmentRequest;
import com.employeeapp.employeeservice.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentResponse> findAll() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    public DepartmentResponse findById(
            @PathVariable Integer id
    ) {
        return departmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(
            @Valid @RequestBody CreateDepartmentRequest request
    ) {
        return departmentService.create(request);
    }

    @PatchMapping("/{id}")
    public DepartmentResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDepartmentRequest request
    ) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id
    ) {
        departmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}