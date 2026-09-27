package com.employeeapp.employeeservice.audit.service;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuditEventConsumerTest {

    @Test
    void consumerStoresReceivedEmployeeProfileUpdate() {
        AuditService auditService = mock(AuditService.class);
        AuditEventConsumer consumer = new AuditEventConsumer(auditService);
        EmployeeProfileUpdatedEvent event = new EmployeeProfileUpdatedEvent(
                UUID.randomUUID(),
                EmployeeProfileUpdatedEvent.EVENT_TYPE,
                4,
                8,
                List.of("phone"),
                OffsetDateTime.now());

        consumer.consumeEmployeeProfileUpdated(event);

        verify(auditService).logEmployeeProfileUpdated(event);
    }
}
