package com.example.ecommerce.service.query.nosql.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "product_search")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDocument {

    @Id
    private Long id;

    private String name;

    private String shortDescription;

    private String fullDescription;

    private String materials;

    private String status;

    private BigDecimal basePrice;

    private BigDecimal salePrice;

    private List<Long> categoryIds;

    private Long sellerId;

    private Long brandId;

    private List<Long> tagIds;

    private Boolean inStock;

    private Instant createdAt;

    private Instant updatedAt;

    private Double averageRating;

    private Integer reviewCount;

    private String slug;
}
