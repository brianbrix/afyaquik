package com.afyaquik.communication.service;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;

import java.util.List;

public interface NotificationService {
    NotificationDto sendToUser(NotificationDto notificationDto);
    List<NotificationDto> getUnread(Long userId, String roleName);
    void markAsRead(Long id);
    void markAllAsRead(Long userId, String roleName);
}
