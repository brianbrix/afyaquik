package com.afyaquik.users.repository;

import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAllByIdIn(List<Long> ids);
    boolean existsByUsername(String username);

    List<User> findByRolesIn(Collection<Role> roles);
}
