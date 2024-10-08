package com.sunny.catalog_service.entities;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    private String description;
    private double price;

    @Transient
    private boolean inStock = true;
}