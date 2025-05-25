package com.afyaquik.web.api.users;


import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.dto.UserResponse;
import com.afyaquik.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponse> createUser(@Validated @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.updateUserDetails(userId, request));
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.fetchById(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers( @RequestParam(required = false) List<Long> ids,
                                          @RequestParam(required = false) String sortBy,
                                          @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                          @RequestParam(required = false) String rangeField,
                                          @RequestParam(required = false) Integer rangeMin,
                                          @RequestParam(required = false) Integer rangeMax,
                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer size ) {

        Page<UserResponse> users = userService.getFilteredUsers(ids, sortBy, sortDir, rangeField, rangeMin, rangeMax, page, size);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "users " + (page * size) + "-" + ((page * size) + users.getNumberOfElements() - 1) + "/" + users.getTotalElements());
        headers.add("Access-Control-Expose-Headers","Content-Range");
        return ResponseEntity.ok()
                .headers(headers)
                .body(users);
    }
    @GetMapping
    public ResponseEntity<?> getAllUsersPaginated(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/byrole")
    public ResponseEntity<?> getAllUsersByRole(@RequestParam Long roleId) {
        return ResponseEntity.ok(userService.getUsersByRole(roleId));
    }


//    @PutMapping("/{userId}/roles")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
//    public ResponseEntity<?> assignRoles(@PathVariable Long userId, @RequestBody AssignRolesRequest request) {
//        return ResponseEntity.ok(userService.assignRoles(userId, request));
//
//    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.fetchByUsername(username));
    }
}
