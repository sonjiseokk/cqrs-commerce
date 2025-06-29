package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_option_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    // -------------------
    // Helper method
    public void addOption(ProductOption option) {
        option.addOptionGroup(this);
        options.add(option);
    }

    public void update(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }
}
