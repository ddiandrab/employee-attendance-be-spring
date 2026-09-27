package com.employeeapp.employeeservice.audit.service;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final KafkaTemplate<String, EmployeeProfileUpdatedEvent> kafkaTemplate;

    @Value("${app.kafka.employee-audit-topic}")
    private String topic;

    public void publishEmployeeProfileUpdated(EmployeeProfileUpdatedEvent event) {
        kafkaTemplate.send(topic, event.eventId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish audit event {}", event.eventId(), exception);
                    }
                });
    }
}
