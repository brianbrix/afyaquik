package com.afyaquik.communication.service;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;

import java.util.List;

public interface NotificationService {
    NotificationDto sendToUser(Long userId, String title, String message, String url, String type);
    List<NotificationDto> getUnread(Long userId);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
}
