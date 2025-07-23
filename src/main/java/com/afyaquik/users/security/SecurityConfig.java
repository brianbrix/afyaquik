package com.afyaquik.users.security;


import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.ApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;
import java.util.stream.Collectors;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableJpaAuditing
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final UsersRepository userRepository;
    private final ApiPermissionService apiPermissionService;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var authorizeRequests = http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // Public endpoints that don't require authentication
                    auth.requestMatchers("/").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/client/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/test/**").permitAll();

                    // Get all API permissions from the database
                    var permissions = apiPermissionService.getEnabledPermissions();

                    // Configure role-based access for each API endpoint
                    for (var permission : permissions) {
                        var urlPattern = permission.getUrlPattern();
                        var roles = permission.getRoleNames().stream()
                                .map(role -> "ROLE_" + role)
                                .toArray(String[]::new);
                        if (roles.length > 0) {
                            auth.requestMatchers(urlPattern).hasAnyAuthority(roles);
                        }
                    }

                    // Any other request requires authentication
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.isEnabled(),
                    true, true, true,
                    user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                            .collect(Collectors.toSet())
            );
        };
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
