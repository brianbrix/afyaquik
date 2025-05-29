package com.afyaquik.communication.service.impl;

import com.afyaquik.communication.dto.NotificationDto;
import com.afyaquik.communication.entity.Notification;
import com.afyaquik.communication.enums.NotificationType;
import com.afyaquik.communication.repository.NotificationRepository;
import com.afyaquik.communication.service.NotificationService;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RolesRepository;
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
    private final RolesRepository rolesRepository;
    private final NotificationMapper mapper;
    @Override
    public NotificationDto sendToUser(NotificationDto  notificationDto) {
        User user = usersRepo.findById(notificationDto.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Role role = rolesRepository.findByName(notificationDto.getRecipientRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Notification notification = Notification.builder()
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .targetUrl(notificationDto.getTargetUrl())
                .recipient(user)
                .recipientRole(role)
                .type(NotificationType.valueOf(notificationDto.getType()))
                .read(false)
                .build();
        return mapper.toDto(notificationRepo.save(notification));
    }


    @Override
    public List<NotificationDto> getUnread(Long userId, String roleName) {
        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Role role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        return notificationRepo.findByRecipientAndRecipientRoleAndReadFalseOrderByCreatedAtDesc(user,role)
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
    public void markAllAsRead(Long userId, String roleName) {

        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Role role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        List<Notification> notifications = notificationRepo.findByRecipientAndRecipientRoleAndReadFalseOrderByCreatedAtDesc(user,role);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepo.saveAll(notifications);
    }
}
