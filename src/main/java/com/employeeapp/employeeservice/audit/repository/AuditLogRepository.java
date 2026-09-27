package com.employeeapp.employeeservice.audit.repository;

import com.employeeapp.employeeservice.audit.event.EmployeeProfileUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogRepository {

    private static final String CREATE_TABLE = """
            create table if not exists audit_logs (
                id bigserial primary key,
                event_id uuid not null unique,
                event_type varchar(100) not null,
                user_id integer not null,
                employee_id integer not null,
                payload jsonb not null,
                created_at timestamptz not null default now()
            )
            """;

    private final JdbcTemplate auditJdbcTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void initializeSchema() {
        auditJdbcTemplate.execute(CREATE_TABLE);
    }

    public void create(EmployeeProfileUpdatedEvent event) {
        String payload = serialize(event);
        auditJdbcTemplate.update("""
                insert into audit_logs (event_id, event_type, user_id, employee_id, payload)
                values (?, ?, ?, ?, cast(? as jsonb))
                on conflict (event_id) do nothing
                """,
                event.eventId(),
                event.eventType(),
                event.userId(),
                event.employeeId(),
                payload);
    }

    private String serialize(EmployeeProfileUpdatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize audit event", exception);
        }
    }
}
