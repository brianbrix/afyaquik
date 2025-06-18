package com.afyaquik.pharmacy.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "drug_forms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugForm extends SuperEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
}
