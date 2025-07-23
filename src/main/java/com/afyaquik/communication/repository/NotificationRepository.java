package com.afyaquik.communication.repository;

import com.afyaquik.communication.entity.Notification;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndRecipientRoleAndReadFalseOrderByCreatedAtDesc(User recipient, Role role);
}
