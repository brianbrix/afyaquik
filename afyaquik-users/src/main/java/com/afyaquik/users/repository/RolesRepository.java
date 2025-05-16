package com.afyaquik.users.repository;

import com.afyaquik.users.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RolesRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
    @Override
    Set<Role> findAll();
}
