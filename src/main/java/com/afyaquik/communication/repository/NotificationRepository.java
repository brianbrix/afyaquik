package com.afyaquik.communication.repository;

import com.afyaquik.communication.entity.Notification;
import com.afyaquik.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndReadFalseOrderByCreatedAtDesc(User recipient);
}
