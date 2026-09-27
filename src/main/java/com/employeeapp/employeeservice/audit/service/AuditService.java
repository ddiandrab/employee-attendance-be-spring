package com.employeeapp.employeeservice.audit.service;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import com.employeeapp.employeeservice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logEmployeeProfileUpdated(EmployeeProfileUpdatedEvent event) {
        auditLogRepository.create(event);
    }
}
