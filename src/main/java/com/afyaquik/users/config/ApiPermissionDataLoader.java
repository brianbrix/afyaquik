package com.afyaquik.users.config;

import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.repository.ApiPermissionRepository;
import com.afyaquik.users.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiPermissionDataLoader {

    private final ApiPermissionRepository apiPermissionRepository;
    private final RolesRepository rolesRepository;

    @Bean
    public CommandLineRunner loadApiPermissions() {
        return args -> {
            if (apiPermissionRepository.count() > 0) {
                log.info("API permissions already exist, skipping initialization");
                return;
            }

            log.info("Initializing API permissions...");

            // Get all roles
            Role receptionist = getOrCreateRole("RECEPTIONIST");
            Role doctor = getOrCreateRole("DOCTOR");
            Role nurse = getOrCreateRole("NURSE");
            Role admin = getOrCreateRole("ADMIN");
            Role superadmin = getOrCreateRole("SUPERADMIN");
            Role pharmacist = getOrCreateRole("PHARMACIST");
            Role cashier = getOrCreateRole("CASHIER");
            Role labTechnician = getOrCreateRole("LAB_TECHNICIAN");

            // Create API permissions
            createApiPermission("/api/search/**",
                "Search API endpoints",
                receptionist, doctor, nurse, admin, superadmin, pharmacist);

            createApiPermission("/api/admin/**",
                "Admin API endpoints",
                admin, superadmin);

            createApiPermission("/api/s_admin/**",
                "Super Admin API endpoints",
                superadmin);

            createApiPermission("/api/doctor/**",
                "Doctor API endpoints",
                doctor);

            createApiPermission("/api/reception/**",
                "Reception API endpoints",
                receptionist);

            createApiPermission("/api/patients/**",
                "Patient API endpoints",
                receptionist, doctor, nurse, admin);

            createApiPermission("/api/cashier/**",
                "Cashier API endpoints",
                cashier);

            createApiPermission("/api/pharmacy/**",
                "Pharmacy API endpoints",
                pharmacist);

            createApiPermission("/api/lab/**",
                "Lab API endpoints",
                labTechnician);

            log.info("API permissions initialized successfully");
        };
    }

    private Role getOrCreateRole(String roleName) {
        return rolesRepository.findByName(roleName)
                .orElseGet(() -> {
                    log.info("Creating role: {}", roleName);
                    Role role = new Role();
                    role.setName(roleName);
                    return rolesRepository.save(role);
                });
    }

    private void createApiPermission(String urlPattern, String description, Role... roles) {
        ApiPermission permission = new ApiPermission();
        permission.setUrlPattern(urlPattern);
        permission.setDescription(description);
        permission.setEnabled(true);
        permission.setAllowedRoles(new HashSet<>(Arrays.asList(roles)));
        apiPermissionRepository.save(permission);
        log.info("Created API permission: {} with roles: {}", urlPattern,
                Arrays.stream(roles).map(Role::getName).toList());
    }
}
