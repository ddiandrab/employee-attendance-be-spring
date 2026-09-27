package com.employeeapp.employeeservice.audit.event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EmployeeProfileUpdatedEvent(
        UUID eventId,
        String eventType,
        Integer userId,
        Integer employeeId,
        List<String> changedFields,
        OffsetDateTime timestamp) {

    public static final String EVENT_TYPE = "EMPLOYEE_PROFILE_UPDATED";
}
