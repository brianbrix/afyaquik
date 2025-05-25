package com.afyaquik.web.api.communication;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationDto>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @PostMapping("/mark-read/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        ResponseEntity.ok();
    }
    @PostMapping("mark-all-read/{userId}")
    public void markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        ResponseEntity.ok();
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationDto> sendNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.sendToUser(notificationDto.getRecipientId(), notificationDto.getTitle(), notificationDto.getMessage(), notificationDto.getTargetUrl(), notificationDto.getType()));
    }
}
