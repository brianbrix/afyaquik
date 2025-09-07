package com.afyaquik.users.service;

import com.afyaquik.users.dto.PasswordResetRequestDto;
import org.springframework.transaction.annotation.Transactional;

public interface PasswordResetRequestService {
    void createPasswordResetRequest(PasswordResetRequestDto request);
    void processPasswordReset(PasswordResetRequestDto request);

    @Transactional
    void closeExpiredRequests();

    String getLatestStatusForUser(Long userId);
}
