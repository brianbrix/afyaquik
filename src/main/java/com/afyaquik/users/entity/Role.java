package com.afyaquik.users.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "allowedRoles", fetch =  FetchType.LAZY)
    @Builder.Default
    private Set<Station> stations = new HashSet<>();
}
