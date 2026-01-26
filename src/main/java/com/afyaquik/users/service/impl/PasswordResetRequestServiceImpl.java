package com.afyaquik.users.service.impl;

import com.afyaquik.users.dto.PasswordResetRequestDto;
import com.afyaquik.users.entity.PasswordRequestStatus;
import com.afyaquik.users.entity.PasswordResetRequest;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.PasswordResetRequestRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.PasswordResetRequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createPasswordResetRequest(PasswordResetRequestDto requestDto) {
        User user = usersRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (passwordResetRequestRepository.findByUserAndStatus(user, PasswordRequestStatus.PENDING).isPresent()) {
            throw new IllegalStateException("Password reset request already pending");
        }
        java.time.LocalDateTime expiryDate = java.time.LocalDateTime.now().plusHours(3);
        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
                .user(user)
                .status(PasswordRequestStatus.PENDING)
                .expiryDate(expiryDate)
                .build();
        passwordResetRequestRepository.save(resetRequest);
    }

    @Override
    @Transactional
    public void processPasswordReset(PasswordResetRequestDto requestDto) {
        User user = usersRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        PasswordResetRequest resetRequest = passwordResetRequestRepository.findByUserAndStatus(user, PasswordRequestStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Pending password reset request not found for: "+user.getUsername()));
        if (resetRequest.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Reset token is invalid or expired");
        }
        if (requestDto.getPassword()==null || requestDto.getConfirmPassword()==null){
            throw new IllegalArgumentException("Password and confirm password are required");
        }
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        usersRepository.save(user);
        resetRequest.setStatus(PasswordRequestStatus.COMPLETED);
        passwordResetRequestRepository.save(resetRequest);
    }

    @Transactional
    @Override
    public void closeExpiredRequests() {
        List<PasswordResetRequest> expiredRequests = passwordResetRequestRepository.findByStatusAndExpiryDateBefore(
                PasswordRequestStatus.PENDING, java.time.LocalDateTime.now());
        for (PasswordResetRequest request : expiredRequests) {
            request.setStatus(PasswordRequestStatus.EXPIRED);
        }
        passwordResetRequestRepository.saveAll(expiredRequests);
    }

    @Scheduled(cron = "0 0 * * * *") // runs every hour
    public void scheduledCloseExpiredRequests() {
        closeExpiredRequests();
    }

    @Override
    public String getLatestStatusForUser(Long userId) {
        return passwordResetRequestRepository.findTopByUserIdOrderByExpiryDateDesc(userId)
                .map(req -> req.getStatus().name())
                .orElse(null);
    }
}
