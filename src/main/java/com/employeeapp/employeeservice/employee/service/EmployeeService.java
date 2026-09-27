package com.employeeapp.employeeservice.employee.service;

import com.employeeapp.employeeservice.common.exception.DuplicateResourceException;
import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import com.employeeapp.employeeservice.audit.service.AuditEventPublisher;

import com.employeeapp.employeeservice.department.entity.Department;
import com.employeeapp.employeeservice.department.repository.DepartmentRepository;

import com.employeeapp.employeeservice.employee.dto.CreateEmployeeRequest;
import com.employeeapp.employeeservice.employee.dto.EmployeeResponse;
import com.employeeapp.employeeservice.employee.dto.UpdateMyProfileRequest;
import com.employeeapp.employeeservice.employee.entity.Employee;
import com.employeeapp.employeeservice.employee.mapper.EmployeeMapper;
import com.employeeapp.employeeservice.employee.repository.EmployeeRepository;

import com.employeeapp.employeeservice.security.PasswordService;
import com.employeeapp.employeeservice.notification.service.NotificationService;

import com.employeeapp.employeeservice.user.entity.Role;
import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    private final PasswordService passwordService;
    private final EmployeeMapper employeeMapper;
    private final NotificationService notificationService;
    private final AuditEventPublisher auditEventPublisher;

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        validateCreateRequest(request);

        Department department = null;

        if (request.getDepartmentId() != null) {
            department = departmentRepository
                    .findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found"));
        }

        OffsetDateTime now = OffsetDateTime.now();

        // Create User entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordService.hash(request.getPassword()));
        user.setRole(Role.EMPLOYEE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        User savedUser = userRepository.save(user);

        // Create Employee entity
        Employee employee = new Employee();
        employee.setEmployeeNumber(request.getEmployeeNumber());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPhotoUrl(request.getPhotoUrl());
        employee.setPosition(request.getPosition());
        employee.setJoinDate(request.getJoinDate());
        employee.setIsActive(true);
        employee.setUser(savedUser);
        employee.setDepartment(department);
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateMyProfile(String email, UpdateMyProfileRequest request) {
        Employee employee = employeeRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
        List<String> changedFields = new ArrayList<>();

        if (request.getPhone() != null && !request.getPhone().equals(employee.getPhone())) {
            employee.setPhone(request.getPhone());
            changedFields.add("phone");
        }

        if (request.getPhotoUrl() != null && !request.getPhotoUrl().equals(employee.getPhotoUrl())) {
            employee.setPhotoUrl(request.getPhotoUrl());
            changedFields.add("photoUrl");
        }

        if (changedFields.isEmpty()) {
            return employeeMapper.toResponse(employee);
        }

        employee.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        Employee updatedEmployee = employeeRepository.save(employee);
        String employeeName = Stream.of(employee.getFirstName(), employee.getLastName())
                .filter(name -> name != null && !name.isBlank())
                .reduce((firstName, lastName) -> firstName + " " + lastName)
                .orElse("Employee");

        notificationService.notifyEmployeeProfileUpdated(employeeName);
        auditEventPublisher.publishEmployeeProfileUpdated(new EmployeeProfileUpdatedEvent(
                UUID.randomUUID(),
                EmployeeProfileUpdatedEvent.EVENT_TYPE,
                employee.getUser().getId(),
                employee.getId(),
                List.copyOf(changedFields),
                OffsetDateTime.now(ZoneOffset.UTC)));

        return employeeMapper.toResponse(updatedEmployee);
    }

    private void validateCreateRequest(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmployeeNumber(
                request.getEmployeeNumber())) {
            throw new DuplicateResourceException(
                    "Employee number already exists");
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists");
        }
    }
}
