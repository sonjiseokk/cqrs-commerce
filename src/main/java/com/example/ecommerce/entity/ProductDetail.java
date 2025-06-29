package com.example.ecommerce.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "product_details")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    private Double weight;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String dimensions; // JSON: {"width": float, "height": float, "depth": float}

    @Column(length = 500)
    private String materials;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "warranty_info", length = 1000)
    private String warrantyInfo;

    @Column(name = "care_instructions", length = 1000)
    private String careInstructions;

    @Column(name = "additional_info", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String additionalInfo; // JSON object for additional information

    // Helper Method
    public void update(Double weight, String dimensions, String materials, String countryOfOrigin, String warrantyInfo, String careInstructions, String additionalInfo) {
        this.weight = weight;
        this.dimensions = dimensions;
        this.materials = materials;
        this.countryOfOrigin = countryOfOrigin;
        this.warrantyInfo = warrantyInfo;
        this.careInstructions = careInstructions;
        this.additionalInfo = additionalInfo;
    }

}
