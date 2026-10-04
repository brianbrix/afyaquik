package com.afyaquik.users.security;

import com.afyaquik.communication.entity.Notification;
import com.afyaquik.communication.repository.NotificationRepository;
import com.afyaquik.communication.service.impl.NotificationServiceImpl;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.utils.mappers.communication.NotificationMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationAuthorizationTest {
    private final NotificationRepository notifications = mock(NotificationRepository.class);
    private final UsersRepository users = mock(UsersRepository.class);
    private final RolesRepository roles = mock(RolesRepository.class);
    private final NotificationServiceImpl service = new NotificationServiceImpl(notifications, users, roles, mock(NotificationMapper.class));
    private Role nurse;
    private User current;

    @BeforeEach
    void setUp() {
        nurse = new Role();
        nurse.setName("NURSE");
        current = User.builder().id(1L).username("nurse").roles(Set.of(nurse)).enabled(true).build();
        when(users.findByUsername("nurse")).thenReturn(Optional.of(current));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "nurse", null, List.of(new SimpleGrantedAuthority("ROLE_NURSE"))));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cannotReadAnotherUsersMailbox() {
        assertThrows(AccessDeniedException.class, () -> service.getUnread(2L, "NURSE"));
        verifyNoInteractions(notifications);
    }

    @Test
    void cannotImpersonateAnotherRole() {
        assertThrows(AccessDeniedException.class, () -> service.getUnread(1L, "SUPERADMIN"));
        verifyNoInteractions(notifications);
    }

    @Test
    void cannotMarkAnotherUsersNotificationRead() {
        Notification notification = Notification.builder().recipient(User.builder().id(2L).build()).recipientRole(nurse).build();
        when(notifications.findById(10L)).thenReturn(Optional.of(notification));
        assertThrows(AccessDeniedException.class, () -> service.markAsRead(10L));
        assertFalse(notification.isRead());
        verify(notifications, never()).save(any());
    }

    @Test
    void canMarkOwnNotificationRead() {
        Notification notification = Notification.builder().recipient(current).recipientRole(nurse).build();
        when(notifications.findById(10L)).thenReturn(Optional.of(notification));
        service.markAsRead(10L);
        assertTrue(notification.isRead());
        assertNotNull(notification.getReadAt());
        verify(notifications).save(notification);
    }
}