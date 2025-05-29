package com.afyaquik.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class NotificationDto {
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    private Long recipientId;
    @NotBlank(message = "Recipient role is required")
    private String recipientRole;
    @NotBlank(message = "Message is required")
    private String message;
    @NotBlank(message = "Type is required")
    private String type;
    private String aboutWhat;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private String targetUrl;
}
