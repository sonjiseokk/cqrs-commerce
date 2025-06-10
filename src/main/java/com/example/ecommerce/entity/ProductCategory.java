package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "product_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private boolean isPrimary;

    // Helper Method
    public static ProductCategory of(Product product, Category category, boolean isPrimary) {
        ProductCategory pc = new ProductCategory();
        pc.product = product;
        pc.category = category;
        pc.isPrimary = isPrimary;
        return pc;
    }
}
