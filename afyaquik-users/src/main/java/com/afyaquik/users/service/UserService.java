package com.afyaquik.users.service;

import com.afyaquik.users.dto.AssignRolesRequest;
import com.afyaquik.users.dto.CreateUserRequest;
import com.afyaquik.users.dto.UserResponse;
import com.afyaquik.users.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getAllUsers();
    User findByUsername(String username);
    UserResponse assignRoles(Long userId, AssignRolesRequest request);
}
