package com.afyaquik.utils.settings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "general_settings")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GeneralSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
   private String settingKey;
   private String settingValue;
}
