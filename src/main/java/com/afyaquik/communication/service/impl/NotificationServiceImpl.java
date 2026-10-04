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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Role role = rolesRepository.findByName(notificationDto.getRecipientRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        List<User> recipients = notificationDto.getRecipientId() == null
                ? usersRepo.findByRolesIn(List.of(role)).stream().filter(User::isEnabled).toList()
                : List.of(usersRepo.findById(notificationDto.getRecipientId())
                        .orElseThrow(() -> new EntityNotFoundException("Recipient not found")));
        if (recipients.isEmpty() || recipients.stream().anyMatch(user -> !user.isEnabled()
                || user.getRoles().stream().noneMatch(userRole -> role.getName().equals(userRole.getName())))) {
            throw new IllegalArgumentException("No enabled recipient with the requested role.");
        }
        List<Notification> notifications = recipients.stream().map(user -> Notification.builder()
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .targetUrl(notificationDto.getTargetUrl())
                .recipient(user)
                .recipientRole(role)
                .type(NotificationType.valueOf(notificationDto.getType()))
                .read(false)
                .createdAt(LocalDateTime.now())
                .build()).toList();
        return mapper.toDto(notificationRepo.saveAll(notifications).get(0));
    }


    @Override
    public List<NotificationDto> getUnread(Long userId, String roleName) {
                User user = requireMailboxOwner(userId, roleName);
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
                if (notification.getRecipient() == null || notification.getRecipientRole() == null) {
                        throw new AccessDeniedException("This notification does not belong to your mailbox.");
                }
                requireMailboxOwner(notification.getRecipient().getId(), notification.getRecipientRole().getName());
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId, String roleName) {

        User user = requireMailboxOwner(userId, roleName);
        Role role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        List<Notification> notifications = notificationRepo.findByRecipientAndRecipientRoleAndReadFalseOrderByCreatedAtDesc(user,role);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepo.saveAll(notifications);
    }

        private User requireMailboxOwner(Long userId, String roleName) {
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new AccessDeniedException("Authentication required.");
                }
                User current = usersRepo.findByUsername(authentication.getName())
                                .orElseThrow(() -> new AccessDeniedException("Staff account not found."));
                if (!current.getId().equals(userId)
                                || current.getRoles().stream().noneMatch(role -> roleName.equals(role.getName()))) {
                        throw new AccessDeniedException("This notification does not belong to your mailbox.");
                }
                return current;
        }
}
