package com.afyaquik.users.entity;

import com.afyaquik.users.enums.Specialty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_details")
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
