package com.afyaquik.users.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.afyaquik.users.entity.PasswordRequestStatus;
import com.afyaquik.users.entity.PasswordResetRequest;
import com.afyaquik.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    Optional<PasswordResetRequest> findByUserAndStatus(User user, PasswordRequestStatus status);

    List<PasswordResetRequest> findByStatusAndExpiryDateBefore(PasswordRequestStatus status, LocalDateTime expiryDateBefore);
    Optional<PasswordResetRequest> findTopByUserIdOrderByExpiryDateDesc(Long userId);
}

