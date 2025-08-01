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

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository roleRepository;
    private final UsersRepository userRepository;
    private final ApiPermissionRepository apiPermissionRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt
    private final ApiPermissionService apiPermissionService;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("SUPERADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SUPERADMIN");
                    return roleRepository.save(role);
                });

        if (userRepository.findByUsername("afl").isEmpty()) {
            User admin = new User();
            admin.setUsername("afl");
            admin.setEmail("afl@afyaquik.net");
            admin.setFirstName("Afl");
            admin.setSecondName("Afl");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=admin123");
        }
    }
}
