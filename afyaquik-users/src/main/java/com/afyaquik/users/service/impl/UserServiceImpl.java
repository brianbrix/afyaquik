package com.afyaquik.users.service.impl;


import com.afyaquik.dtos.user.AssignRolesRequest;
import com.afyaquik.dtos.user.CreateUserRequest;
import com.afyaquik.dtos.user.UserResponse;
import com.afyaquik.users.entity.RevokedToken;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RevokedTokenRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UsersRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository roleRepository;
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Set<Role> userRoles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstname())
                .secondName(request.getSecondname())
                .email(request.getEmail())
                .lastName(request.getLastname())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(userRoles)
                .build();
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(userRoles.stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllByIdIn(ids).stream()
                    .map(user -> UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .secondName(user.getSecondName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                            .build())
                    .collect(Collectors.toList());
        }
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .secondName(user.getSecondName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    @Override
    public UserResponse fetchByUsername(String username) {
        User user = findByUsername(username);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserResponse assignRoles(Long userId, AssignRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<Role> userRoles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(userRoles);
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(userRoles.stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public List<UserResponse> getUsersByRole(Long roleId) {
        return List.of();
    }

    @Override
    public List<UserResponse> getUsersByRoles(List<Long> roleIds) {
        List<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found")))
                .toList();
        return userRepository.findByRolesIn(roles).stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .secondName(user.getSecondName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void logoutUser(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            RevokedToken revokedToken = new RevokedToken();
            revokedToken.setToken(token);
            revokedToken.setRevokedAt(Instant.now());
            revokedTokenRepository.save(revokedToken);
        }
        else
            {
            throw new IllegalArgumentException("Invalid token.");
        }
    }


}
