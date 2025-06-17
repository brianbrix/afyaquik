package com.afyaquik.pharmacy.entity;

import com.afyaquik.utils.SuperEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drug_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugInventory extends SuperEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private Drug drug;
    private int initialQuantity;// initial quantity of the drug in stock for this batch
    private int currentQuantity;// current quantity of the drug in stock for this batch
    @Column(length = 13, unique = true)
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDateTime receivedDate;
    private double buyingPrice;// price at which the drug was purchased
    private double sellingPrice;// price at which the drug is sold to customers
    private boolean isActive = true;//set to false when the drug is no longer available in the inventory i.e. expired or quantity is zero
    private String supplierName; // name of the supplier from whom the drug was purchased
}
