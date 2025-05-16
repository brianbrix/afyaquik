package com.afyaquik.users.entity;

import com.afyaquik.users.enums.Specialty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nurses")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDetails extends User{

    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

}
