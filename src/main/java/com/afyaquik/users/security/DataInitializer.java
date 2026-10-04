package com.afyaquik.users.security;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.ApiPermissionRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.ApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository roleRepository;
    private final UsersRepository userRepository;
    private final ApiPermissionRepository apiPermissionRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt
    private final ApiPermissionService apiPermissionService;
    @Value("${app.bootstrap.username:afl}")
    private String bootstrapUsername;
    @Value("${app.bootstrap.password:}")
    private String bootstrapPassword;
    @Value("${app.bootstrap.email:admin@example.invalid}")
    private String bootstrapEmail;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        if (bootstrapPassword == null || bootstrapPassword.length() < 12) {
            throw new IllegalStateException("Set APP_BOOTSTRAP_PASSWORD to at least 12 characters before initializing an empty staff database.");
        }
        Role adminRole = roleRepository.findByName("SUPERADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SUPERADMIN");
                    return roleRepository.save(role);
                });

        if (userRepository.findByUsername(bootstrapUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(bootstrapUsername);
            admin.setEmail(bootstrapEmail);
            admin.setFirstName("Facility");
            admin.setSecondName("");
            admin.setLastName("Administrator");
            admin.setEnabled(true);
            admin.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }
}
