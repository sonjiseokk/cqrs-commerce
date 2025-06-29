package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "products")
public class Product {
    // 상품 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품명
    @Column(length = 255, nullable = false)
    private String name;

    // URL 슬러그 (SEO 최적화용)
    @Column(length = 255, unique = true, nullable = false)
    private String slug;

    // 짧은 설명
    @Column(length = 500)
    private String shortDescription;

    // 전체 설명 (HTML 허용)
    private String fullDescription;

    // 등록일
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // 수정일
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // 판매자 ID (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    // 브랜드 ID (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // 상태 (판매중, 품절, 삭제됨 등)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    // 상품 상세 설명들
    // 상품 삭제 시 같이 삭제됨
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductDetail detail;

    // 상품 가격들
    // 상품 삭제 시 같이 삭제됨
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductPrice price;

    // 상품 카테고리들
    // 상품 삭제 시 같이 삭제됨
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductCategory> categories = new ArrayList<>();

    // 상품 옵션 그룹
    // 상품 삭제 시 같이 삭제됨
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductOptionGroup> optionGroups = new ArrayList<>();

    // 상품 이미지들
    // 상품 삭제 시 같이 삭제됨
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    // 상품 태그들
    // 상품 삭제 시 같이 삭제됨
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductTag> tags = new ArrayList<>();

    // 상품 리뷰들
    // 상품 삭제 시 같이 삭제됨
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // ----------------------------------------------
    // Helper methods
    public void connectDetail(ProductDetail detail) {
        this.detail = detail;
    }

    public void connectPrice(ProductPrice price) {
        this.price = price;
    }

    public void connectSeller(Seller seller) {
        this.seller = seller;
    }

    public void connectBrand(Brand brand) {
        this.brand = brand;
    }

    public void update(
            String name,
            String slug,
            String shortDescription,
            String fullDescription,
            String status
    ) {
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.status = ProductStatus.valueOf(status);
    }

    public void eagerLoad(List<ProductOptionGroup> optionGroups, List<ProductTag> tags, List<ProductCategory> categories) {
        // 옵션 그룹
        if (optionGroups != null) {
            this.optionGroups = optionGroups;
        }

        // 태그
        if (tags != null) {
            this.tags = tags;
        }

        // 카테고리
        if (categories != null) {
            this.categories = categories;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public void delete() {
        this.status = ProductStatus.DELETED;
    }



}
