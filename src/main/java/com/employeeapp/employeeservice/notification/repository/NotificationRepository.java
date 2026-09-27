package com.employeeapp.employeeservice.notification.repository;

import com.employeeapp.employeeservice.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId);

    Optional<Notification> findByIdAndRecipientId(Integer id, Integer recipientId);

    @Modifying
    @Query("""
            update Notification notification
            set notification.isRead = true, notification.updatedAt = :updatedAt
            where notification.recipient.id = :recipientId and notification.isRead = false
            """)
    int markAllAsRead(@Param("recipientId") Integer recipientId, @Param("updatedAt") OffsetDateTime updatedAt);
}
