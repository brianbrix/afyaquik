package com.afyaquik.communication.service.impl;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;
import com.afyaquik.communication.enums.NotificationType;
import com.afyaquik.communication.repository.NotificationRepository;
import com.afyaquik.communication.service.NotificationService;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.utils.mappers.communication.NotificationMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepo;
    private final UsersRepository usersRepo;
    private final NotificationMapper mapper;
    @Override
    public NotificationDto sendToUser(Long userId, String title, String message, String url, String type) {
        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .targetUrl(url)
                .recipient(user)
                .type(NotificationType.valueOf(type))
                .read(false)
                .build();
    return mapper.toDto(notificationRepo.save(notification));

    }

    @Override
    public List<NotificationDto> getUnread(Long userId) {
        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return notificationRepo.findByRecipientAndReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {

        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Notification> notifications = notificationRepo.findByRecipientAndReadFalseOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepo.saveAll(notifications);
    }
}
