package com.afyaquik.users.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Station extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

}
