package com.afyaquik.communication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String title;
    private Long recipientId;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private String targetUrl;
}
