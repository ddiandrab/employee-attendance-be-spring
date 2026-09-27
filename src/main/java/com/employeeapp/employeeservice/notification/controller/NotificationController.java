package com.employeeapp.employeeservice.notification.controller;

import com.employeeapp.employeeservice.notification.dto.NotificationResponse;
import com.employeeapp.employeeservice.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> findMine(Authentication authentication) {
        return notificationService.findMine(authentication.getName());
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(
            Authentication authentication,
            @PathVariable Integer id) {
        return notificationService.markAsRead(authentication.getName(), id);
    }

    @PatchMapping("/read-all")
    public boolean markAllAsRead(Authentication authentication) {
        return notificationService.markAllAsRead(authentication.getName());
    }
}
