package com.afyaquik.users.repository;

import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
    boolean existsByEmail(String email);

    List<User> findByRolesIn(Collection<Role> roles);
    Set<User> findByStationsIn(Collection<Station> stations);
    Set<User> findByStationsContaining(Station station);
    
    @Query("SELECT u FROM User u")
    Page<User> getAllUsersPaginated(Pageable pageable);
}
