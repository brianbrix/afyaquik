package com.afyaquik.users.entity.security;

import com.afyaquik.users.entity.Role;
import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "api_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiPermission extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String urlPattern;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "api_permission_roles",
            joinColumns = @JoinColumn(name = "api_permission_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> allowedRoles = new HashSet<>();

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(length = 255)
    private String description;
}
