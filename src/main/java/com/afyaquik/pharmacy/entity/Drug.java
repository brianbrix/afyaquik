package com.afyaquik.pharmacy.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("0.00")
    private double stockQuantity=0.00;
    @Column(nullable = false)
    @ColumnDefault("0.00")
    private Double currentPrice=0.00;
    private boolean isPrescriptionRequired=false;
    private String atcCode;
    @ColumnDefault("true")
    private boolean enabled=true;
}
