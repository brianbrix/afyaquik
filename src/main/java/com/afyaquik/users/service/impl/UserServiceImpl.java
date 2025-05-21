package com.afyaquik.users.service.impl;


import com.afyaquik.utils.exceptions.DuplicateValueException;
import com.afyaquik.users.dto.AssignRolesRequest;
import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.dto.UserResponse;
import com.afyaquik.users.entity.RevokedToken;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RevokedTokenRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.JwtProviderService;
import com.afyaquik.users.service.UserService;
import com.afyaquik.utils.exceptions.LoginException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private final StationRepository stationRepository;
    private final EntityManager entityManager;
    private final JwtProviderService jwtProvider;
    private final AuthenticationManager authenticationManager;
    @Override
    @CacheEvict(value = "searchResults", allEntries = true)
    public UserResponse createUser(UserDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateValueException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateValueException("Email already exists");
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



    public Authentication getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication;
    }
    public List<String> getCurrentUserRoles() {
        List<String> roles= getCurrentUser().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return roles;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        return userDetails.getId();
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
                            .available(user.isAvailable())
                            .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
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
                        .available(user.isEnabled())
                        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                        .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
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
                .available(user.isAvailable())
                .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
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
                        .available(user.isAvailable())
                        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                        .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
                        .build())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @CacheEvict(value = "searchResults", allEntries = true)
    public UserResponse updateUserDetails(Long userId, UserDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        cleanEditDetails(request);
        user.setFirstName(request.getFirstName());
        user.setSecondName(request.getSecondName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEnabled(request.getEnabled());
        user.setEmail(request.getEmail());
        user.setEnabled(request.getEnabled());
        user.setAvailable(request.isAvailable());
        AssignRolesRequest assignRolesRequest= new AssignRolesRequest();
        assignRolesRequest.setRoles(request.getRoles());
        assignRoles(user, assignRolesRequest);
        assignStations(user, request);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .available(user.isAvailable())
                .enabled(user.isEnabled())
                .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    /**
     * This method is used to clean the edit details of the user.
     * It is used to prevent the user from editing some of their own details.
     * If the user is editing their own details, the user will be enabled and the SUPERADMIN will be removed. if they are not superadmin
     * @param userDto
     */
    private void cleanEditDetails(UserDto userDto){
        if (userDto.getUsername().equals(getCurrentUsername()))
        {
            userDto.setEnabled(true);
            userDto.setUsername(userDto.getUsername());
            if (!getCurrentUserRoles().contains("ROLE_SUPERADMIN")) {
                userDto.getRoles().removeIf(x -> x.equals("SUPERADMIN"));
            }
            else
                {
                userDto.getRoles().add("SUPERADMIN");
            }
        }
    }


    private void assignRoles(User user, AssignRolesRequest request) {
        Set<Role> userRoles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(userRoles);

    }
    private void assignStations(User user, UserDto request) {
        if (request.getStations()!=null) {
            Set<Station> userRoles = request.getStations().stream()
                    .map(station -> stationRepository.findByName(station)
                            .orElseThrow(() -> new EntityNotFoundException("Station not found: " + station)))
                    .collect(Collectors.toSet());
            user.setStations(userRoles);
        }
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
                                .enabled(user.isEnabled())
                                .available(user.isAvailable())
                                .email(user.getEmail())
                                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                                .stations(user.getStations().stream().map(Station::getName).collect(Collectors.toSet()))
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



}
