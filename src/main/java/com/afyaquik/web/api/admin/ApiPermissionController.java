package com.afyaquik.web.api.admin;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.service.ApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class ApiPermissionController {

    private final ApiPermissionService apiPermissionService;
    @PostMapping
    public ResponseEntity<ApiPermissionDto> createPermission(@RequestBody ApiPermissionDto apiPermissionDto) {
        return ResponseEntity.ok(apiPermissionService.savePermission(apiPermissionDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable("id") Long id) {
        apiPermissionService.deletePermission(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiPermissionDto> updatePermission(@PathVariable("id") Long id, @RequestBody ApiPermissionDto apiPermissionDto) {
        return ResponseEntity.ok(apiPermissionService.updatePermission( apiPermissionDto, id));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiPermissionDto> getPermission(@PathVariable("id") Long id) {
        return ResponseEntity.ok(apiPermissionService.getPermissionById(id));
    }





}
