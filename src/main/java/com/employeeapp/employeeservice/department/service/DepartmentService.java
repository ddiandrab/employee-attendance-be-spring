package com.employeeapp.employeeservice.department.service;

import com.employeeapp.employeeservice.common.exception.DuplicateResourceException;
import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.department.dto.CreateDepartmentRequest;
import com.employeeapp.employeeservice.department.dto.DepartmentResponse;
import com.employeeapp.employeeservice.department.dto.UpdateDepartmentRequest;
import com.employeeapp.employeeservice.department.entity.Department;
import com.employeeapp.employeeservice.department.mapper.DepartmentMapper;
import com.employeeapp.employeeservice.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    public DepartmentResponse findById(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));
        return departmentMapper.toResponse(department);
    }

    public DepartmentResponse create(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Department already exists with name: "
                            + request.getName());
        }

        Department department = new Department();
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setCreatedAt(OffsetDateTime.now());
        department.setUpdatedAt(OffsetDateTime.now());
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponse(savedDepartment);
    }

    public DepartmentResponse update(
            Integer id,
            UpdateDepartmentRequest request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));

        if (request.getName() != null) {
            if (departmentRepository.existsByNameAndIdNot(
                    request.getName(),
                    id)) {
                throw new DuplicateResourceException(
                        "Department already exists with name: "
                                + request.getName());
            }
            department.setName(request.getName());
        }

        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }
        department.setUpdatedAt(OffsetDateTime.now());

        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponse(updatedDepartment);
    }

    public void delete(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));

        departmentRepository.delete(department);
    }
}