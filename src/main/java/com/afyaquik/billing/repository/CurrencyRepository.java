package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByActiveTrue();

    Optional<Currency> findByCode(String code);

    Optional<Currency> findByName(String name);
}
