package com.employeeapp.employeeservice.employee.service;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import com.employeeapp.employeeservice.audit.service.AuditEventPublisher;
import com.employeeapp.employeeservice.department.repository.DepartmentRepository;
import com.employeeapp.employeeservice.employee.dto.UpdateMyProfileRequest;
import com.employeeapp.employeeservice.employee.entity.Employee;
import com.employeeapp.employeeservice.employee.mapper.EmployeeMapper;
import com.employeeapp.employeeservice.employee.repository.EmployeeRepository;
import com.employeeapp.employeeservice.notification.service.NotificationService;
import com.employeeapp.employeeservice.security.PasswordService;
import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    @Test
    void profileUpdateNotifiesAdministratorsAndPublishesAuditEvent() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        NotificationService notificationService = mock(NotificationService.class);
        AuditEventPublisher auditEventPublisher = mock(AuditEventPublisher.class);
        EmployeeService employeeService = new EmployeeService(
                employeeRepository,
                userRepository,
                departmentRepository,
                passwordService,
                new EmployeeMapper(),
                notificationService,
                auditEventPublisher);

        User user = new User();
        user.setId(4);
        Employee employee = new Employee();
        employee.setId(8);
        employee.setUser(user);
        employee.setFirstName("Rani");
        employee.setLastName("Putri");
        employee.setPhone("0812345678");
        employee.setEmployeeNumber("EMP-008");
        employee.setEmail("rani@example.com");
        employee.setIsActive(true);
        when(employeeRepository.findByUserEmail("rani@example.com")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateMyProfileRequest request = new UpdateMyProfileRequest();
        request.setPhone("0898765432");
        var response = employeeService.updateMyProfile("rani@example.com", request);

        ArgumentCaptor<EmployeeProfileUpdatedEvent> eventCaptor = ArgumentCaptor
                .forClass(EmployeeProfileUpdatedEvent.class);
        verify(notificationService).notifyEmployeeProfileUpdated("Rani Putri");
        verify(auditEventPublisher).publishEmployeeProfileUpdated(eventCaptor.capture());
        assertEquals(EmployeeProfileUpdatedEvent.EVENT_TYPE, eventCaptor.getValue().eventType());
        assertEquals(4, eventCaptor.getValue().userId());
        assertEquals(8, eventCaptor.getValue().employeeId());
        assertEquals(java.util.List.of("phone"), eventCaptor.getValue().changedFields());
        assertEquals("0898765432", response.getPhone());
    }

    @Test
    void unchangedProfileDoesNotPublishAuditEvent() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        NotificationService notificationService = mock(NotificationService.class);
        AuditEventPublisher auditEventPublisher = mock(AuditEventPublisher.class);
        EmployeeService employeeService = new EmployeeService(
                employeeRepository,
                userRepository,
                departmentRepository,
                passwordService,
                new EmployeeMapper(),
                notificationService,
                auditEventPublisher);

        User user = new User();
        user.setId(4);
        Employee employee = new Employee();
        employee.setId(8);
        employee.setUser(user);
        employee.setFirstName("Rani");
        employee.setEmployeeNumber("EMP-008");
        employee.setEmail("rani@example.com");
        employee.setPhone("0812345678");
        employee.setIsActive(true);
        when(employeeRepository.findByUserEmail("rani@example.com")).thenReturn(Optional.of(employee));

        UpdateMyProfileRequest request = new UpdateMyProfileRequest();
        request.setPhone("0812345678");
        employeeService.updateMyProfile("rani@example.com", request);

        verify(notificationService, never()).notifyEmployeeProfileUpdated(any());
        verify(auditEventPublisher, never()).publishEmployeeProfileUpdated(any());
        verify(employeeRepository, never()).save(any());
    }
}
