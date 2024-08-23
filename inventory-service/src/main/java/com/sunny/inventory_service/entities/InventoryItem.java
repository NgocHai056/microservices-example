package com.sunny.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "inventory")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @Column(name = "quantity")
    private Integer availableQuantity = 0;
}
