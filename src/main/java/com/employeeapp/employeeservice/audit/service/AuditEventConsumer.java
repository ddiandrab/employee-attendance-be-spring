package com.employeeapp.employeeservice.audit.service;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditService auditService;

    @KafkaListener(topics = "${app.kafka.employee-audit-topic}")
    public void consumeEmployeeProfileUpdated(EmployeeProfileUpdatedEvent event) {
        auditService.logEmployeeProfileUpdated(event);
    }
}
