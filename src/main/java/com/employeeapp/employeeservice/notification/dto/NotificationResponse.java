package com.employeeapp.employeeservice.notification.dto;

import com.employeeapp.employeeservice.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Integer id;
    private Integer recipientId;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
