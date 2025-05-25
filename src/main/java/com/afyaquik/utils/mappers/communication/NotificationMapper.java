package com.afyaquik.utils.mappers.communication;


import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<Notification, NotificationDto> {
    @Override
    NotificationDto toDto(Notification notification);

}
