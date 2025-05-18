package com.afyaquik.users.repository;

import com.afyaquik.users.entity.RevokedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, Long>{
    boolean existsByToken(String token);
}
