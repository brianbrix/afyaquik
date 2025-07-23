package com.afyaquik.web.api.communication;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationDto>> getUnread(@PathVariable Long userId, @RequestParam String roleName) {
        return ResponseEntity.ok(notificationService.getUnread(userId, roleName));
    }

    @PutMapping("/mark-read/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        ResponseEntity.ok();
    }
    @PutMapping("mark-all-read/{userId}")
    public void markAllAsRead(@PathVariable Long userId, @RequestParam String roleName) {
        notificationService.markAllAsRead(userId,roleName);
        ResponseEntity.ok();
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationDto> sendNotification(@Validated @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.sendToUser(notificationDto));
    }
}
