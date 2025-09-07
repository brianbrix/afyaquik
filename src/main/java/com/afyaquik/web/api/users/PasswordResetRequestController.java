package com.afyaquik.web.api.users;

import com.afyaquik.users.dto.PasswordResetRequestDto;
import com.afyaquik.users.service.PasswordResetRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
public class PasswordResetRequestController {
    private final PasswordResetRequestService passwordResetRequestService;

    @PostMapping("/request")
    public ResponseEntity<Void> createPasswordResetRequest(@RequestParam PasswordResetRequestDto requestDto) {
        passwordResetRequestService.createPasswordResetRequest(requestDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/process")
    public ResponseEntity<Void> processPasswordReset(@RequestBody PasswordResetRequestDto requestDto) {
        passwordResetRequestService.processPasswordReset(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<String> getPasswordResetStatus(@PathVariable Long userId) {
        String status = passwordResetRequestService.getLatestStatusForUser(userId);
        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
