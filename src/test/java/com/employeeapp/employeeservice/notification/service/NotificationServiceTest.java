package com.employeeapp.employeeservice.notification.service;

import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.notification.entity.Notification;
import com.employeeapp.employeeservice.notification.entity.NotificationType;
import com.employeeapp.employeeservice.notification.repository.NotificationRepository;
import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final NotificationService notificationService = new NotificationService(notificationRepository, userRepository);
    private final User user = new User();

    @BeforeEach
    void setUp() {
        user.setId(8);
        user.setEmail("hr@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void findMineReturnsOnlyAuthenticatedUsersNotifications() {
        Notification notification = notification(12, false);
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(8)).thenReturn(List.of(notification));

        var result = notificationService.findMine(user.getEmail());

        assertEquals(1, result.size());
        assertEquals(12, result.getFirst().getId());
        assertEquals(8, result.getFirst().getRecipientId());
        verify(notificationRepository).findByRecipientIdOrderByCreatedAtDesc(8);
    }

    @Test
    void markAsReadUpdatesOwnedUnreadNotification() {
        Notification notification = notification(12, false);
        when(notificationRepository.findByIdAndRecipientId(12, 8)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = notificationService.markAsRead(user.getEmail(), 12);

        assertEquals(true, result.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsReadHidesNotificationsOwnedByAnotherUser() {
        when(notificationRepository.findByIdAndRecipientId(12, 8)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(user.getEmail(), 12));

        assertEquals("Notification not found", exception.getMessage());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void markAllAsReadUpdatesOnlyTheAuthenticatedUsersUnreadNotifications() {
        boolean result = notificationService.markAllAsRead(user.getEmail());

        assertEquals(true, result);
        verify(notificationRepository).markAllAsRead(eq(8), any(OffsetDateTime.class));
    }

    private Notification notification(Integer id, boolean isRead) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipient(user);
        notification.setType(NotificationType.EMPLOYEE_PROFILE_UPDATED);
        notification.setTitle("Employee Profile Updated");
        notification.setMessage("A profile was updated.");
        notification.setIsRead(isRead);
        notification.setCreatedAt(OffsetDateTime.now());
        notification.setUpdatedAt(OffsetDateTime.now());
        return notification;
    }
}
