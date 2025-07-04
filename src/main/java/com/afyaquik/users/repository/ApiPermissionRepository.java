package com.afyaquik.users.repository;

import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiPermissionRepository extends CrudRepository<ApiPermission, Long> {
    Optional<ApiPermission> findByUrlPattern(String urlPattern);
    List<ApiPermission> findByEnabledTrue();
    List<ApiPermission> findByAllowedRolesContaining(Role role);
    @Override
    List<ApiPermission> findAll();
}
