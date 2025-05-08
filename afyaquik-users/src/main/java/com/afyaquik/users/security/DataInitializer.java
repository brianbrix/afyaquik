package com.afyaquik.users.security;

import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository roleRepository;
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_SUPERADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_SUPERADMIN")));

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
