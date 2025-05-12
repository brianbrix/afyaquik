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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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
    private final EntityManager entityManager;
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
                .firstName(request.getFirstName())
                .secondName(request.getSecondName())
                .email(request.getEmail())
                .enabled(request.getEnabled())
                .lastName(request.getLastName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(userRoles)
                .build();
        user = userRepository.save(user);
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
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    @Override
    @Deprecated(forRemoval = true)
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
                            .enabled(user.isEnabled())
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
                        .enabled(user.isEnabled())
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
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserResponse fetchById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .secondName(user.getSecondName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .enabled(user.isEnabled())
                        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                        .build())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public UserResponse updateUserDetails(Long userId, CreateUserRequest  request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setSecondName(request.getSecondName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEnabled(request.getEnabled());
        user.setEmail(request.getEmail());
        user.setEnabled(request.getEnabled());
        AssignRolesRequest assignRolesRequest= new AssignRolesRequest();
        assignRolesRequest.setRoles(request.getRoles());
        assignRoles(user, assignRolesRequest);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }


    private void assignRoles(User user, AssignRolesRequest request) {
        Set<Role> userRoles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(userRoles);
        userRepository.save(user);

    }

    @Override
    public List<UserResponse> getUsersByRole(Long roleId) {
        return List.of();
    }

    @Override
    public Page<UserResponse> getFilteredUsers(List<Long> ids, String sortBy, String sortDir, String rangeField, Integer rangeMin, Integer rangeMax, Integer page, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            predicates.add(root.get("id").in(ids));
        }

        if (rangeField != null && rangeMin != null && rangeMax != null) {
            predicates.add(cb.between(root.get(rangeField), rangeMin, rangeMax));
        } else if (rangeField != null && rangeMin != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(rangeField), rangeMin));
        } else if (rangeField != null && rangeMax != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(rangeField), rangeMax));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // Sorting
        if (sortBy != null) {
            if ("desc".equalsIgnoreCase(sortDir)) {
                cq.orderBy(cb.desc(root.get(sortBy)));
            } else {
                cq.orderBy(cb.asc(root.get(sortBy)));
            }
        }

        // Query execution with pagination
        TypedQuery<User> query = entityManager.createQuery(cq);

        // Pagination defaults
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? size : 10;
        int offset = pageNumber * pageSize;

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        // Fetch results
        List<User> resultList = query.getResultList();

        // For total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(
                resultList.stream()
                        .map(user -> UserResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .firstName(user.getFirstName())
                                .secondName(user.getSecondName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()),
                PageRequest.of(pageNumber, pageSize),
                totalCount
        );
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
