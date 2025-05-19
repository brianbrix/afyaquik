package com.afyaquik.users.service;


import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.dto.UserResponse;
import com.afyaquik.users.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserDto request);
    List<UserResponse> getAllUsers(List<Long> ids);
    User findByUsername(String username);
    HttpHeaders login(String username, String password);
    void logout(HttpServletRequest request, HttpServletResponse response);
    UserResponse fetchByUsername(String username);
    UserResponse fetchById(Long userId);
    UserResponse updateUserDetails(Long userId, UserDto request);
//    UserResponse assignRoles(Long userId, AssignRolesRequest request);
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
    String getCurrentUsername();
}
