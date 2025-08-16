package com.afyaquik.web.api.users;

import com.afyaquik.users.dto.RoleRequest;
import com.afyaquik.users.dto.RoleResponse;
import com.afyaquik.users.service.UserRoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final UserRoleService userRoleService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(userRoleService.createRole(roleRequest));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(userRoleService.getRole(id));
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
        List<RoleResponse> roles = userRoleService.getAllRoles();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "roles " + 1 + "-" + roles.size() + "/" + roles.size());
        headers.add("Access-Control-Expose-Headers","Content-Range");
        return ResponseEntity.ok().headers(headers).body(roles);
    }
    @GetMapping("/byName/{name}")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<?> getRoleByName(@PathVariable String name) {
        return  ResponseEntity.ok(userRoleService.getRoleByName(name));
    }

}
