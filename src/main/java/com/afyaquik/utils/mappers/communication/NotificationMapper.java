package com.afyaquik.utils.mappers.communication;


import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<Notification, NotificationDto> {
    default NotificationDto toDto(Notification entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType().name())
                .read(entity.isRead())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .targetUrl(entity.getTargetUrl())
                .recipientId(entity.getRecipient().getId())
                .recipientRole(entity.getRecipientRole().getName())
                .build();
    }

}
