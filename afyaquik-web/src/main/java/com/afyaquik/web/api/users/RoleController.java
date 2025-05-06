package com.afyaquik.web.api.users;

import com.afyaquik.dtos.user.RoleRequest;
import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")//TODO: specify origins
public class RoleController {
    private final UserRoleService userRoleService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(userRoleService.createRole(roleRequest));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id,@RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(userRoleService.updateRole(id, roleRequest));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        userRoleService.deleteRole(id);
        return ResponseEntity.ok(null);
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(userRoleService.getAllRoles());
    }
}
