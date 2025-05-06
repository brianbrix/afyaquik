package com.afyaquik.web.api.users;


import com.afyaquik.dtos.user.AssignRolesRequest;
import com.afyaquik.dtos.user.CreateUserRequest;
import com.afyaquik.dtos.user.UserResponse;
import com.afyaquik.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")//TODO: specify origins
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers( @RequestParam(required = false) List<Long> ids,
                                          @RequestParam(required = false) String sortBy,
                                          @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                          @RequestParam(required = false) String rangeField,
                                          @RequestParam(required = false) Integer rangeMin,
                                          @RequestParam(required = false) Integer rangeMax,
                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer size ) {
        return ResponseEntity.ok(userService.getFilteredUsers(ids, sortBy, sortDir, rangeField, rangeMin, rangeMax, page, size));
    }
    @GetMapping("/byrole")
    public ResponseEntity<?> getAllUsersByRole(@RequestParam Long roleId) {
        return ResponseEntity.ok(userService.getUsersByRole(roleId));
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<?> assignRoles(@PathVariable Long userId, @RequestBody AssignRolesRequest request) {
        return ResponseEntity.ok(userService.assignRoles(userId, request));

    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.fetchByUsername(username));
    }
}
