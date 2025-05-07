package com.afyaquik.users.service;


import com.afyaquik.dtos.user.AssignRolesRequest;
import com.afyaquik.dtos.user.CreateUserRequest;
import com.afyaquik.dtos.user.UserResponse;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getAllUsers(List<Long> ids);
    User findByUsername(String username);
    UserResponse fetchByUsername(String username);
    UserResponse fetchById(Long userId);
    UserResponse updateUserDetails(Long userId, CreateUserRequest  request);
    UserResponse assignRoles(Long userId, AssignRolesRequest request);
    List<UserResponse> getUsersByRole(Long roleId);
    Page<UserResponse> getFilteredUsers(
            List<Long> ids,
            String sortBy,
            String sortDir,
            String rangeField,
            Integer rangeMin,
            Integer rangeMax,
            Integer page,
            Integer size
    );
    List<UserResponse> getUsersByRoles(List<Long> roleIds);
    void logoutUser(String authHeader);
}
