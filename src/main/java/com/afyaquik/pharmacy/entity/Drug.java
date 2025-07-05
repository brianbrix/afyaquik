package com.afyaquik.pharmacy.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drugs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drug extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brandName;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "drug_category_id")
    private DrugCategory drugCategory;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "drug_form_id")
    private DrugForm drugForm;
    private String strength;
    private String manufacturer;
    private String sampleDosageInstruction;
    private double stockQuantity=0;
    private boolean isPrescriptionRequired=false;
    private String atcCode;
    private boolean enabled=true;
}
