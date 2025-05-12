package com.afyaquik.users.entity;

import com.afyaquik.dtos.SuperEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Data
public class RevokedToken extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant revokedAt;

}
