package com.employeeapp.employeeservice.notification.service;

import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.notification.dto.NotificationResponse;
import com.employeeapp.employeeservice.notification.entity.Notification;
import com.employeeapp.employeeservice.notification.entity.NotificationType;
import com.employeeapp.employeeservice.notification.repository.NotificationRepository;
import com.employeeapp.employeeservice.user.entity.Role;
import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findMine(String email) {
        User user = findUserByEmail(email);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(String email, Integer notificationId) {
        User user = findUserByEmail(email);
        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            notification = notificationRepository.save(notification);
        }

        return toResponse(notification);
    }

    @Transactional
    public boolean markAllAsRead(String email) {
        User user = findUserByEmail(email);
        notificationRepository.markAllAsRead(user.getId(), OffsetDateTime.now(ZoneOffset.UTC));
        return true;
    }

    @Transactional
    public void notifyEmployeeProfileUpdated(String employeeName) {
        List<User> recipients = userRepository.findByRoleIn(List.of(Role.ADMIN, Role.HR));
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        for (User recipient : recipients) {
            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setType(NotificationType.EMPLOYEE_PROFILE_UPDATED);
            notification.setTitle("Employee Profile Updated");
            notification.setMessage(employeeName + " updated their profile information.");
            notification.setIsRead(false);
            notification.setCreatedAt(now);
            notification.setUpdatedAt(now);
            notificationRepository.save(notification);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipient().getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
