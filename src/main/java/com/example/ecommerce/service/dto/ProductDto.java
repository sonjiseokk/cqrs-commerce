package com.example.ecommerce.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductDto {
    /** Product 상세 조회용 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetail {
        private Long id;
        private String name;
        private String slug;
        private String shortDescription;
        private String fullDescription;
        private SellerDetail seller;
        private BrandDetail brand;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Detail detail;
        private Price price;
        private List<CategorySummary> categories;
        private List<OptionGroup> optionGroups;
        private List<ImageDetail> images;
        private List<Tag> tags;
        private RatingSummary rating;
        private List<ProductSummary> relatedProducts;
    }

    /** Product 목록 조회용 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSummary {
        private Long id;
        private String name;
        private String slug;
        private String shortDescription;
        private BigDecimal basePrice;
        private BigDecimal salePrice;
        private String currency;
        private ImageSummary primaryImage;
        private BrandSummary brand;
        private SellerSummary seller;
        private Double rating;
        private Integer reviewCount;
        private boolean inStock;
        private String status;
        private LocalDateTime createdAt;
    }

    /**
     * Seller 요약
     * - Product 목록 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerSummary {
        private Long id;
        private String name;
    }

    /**
     * Seller 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerDetail {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private Double rating;
        private String contactEmail;
        private String contactPhone;
    }

    /**
     * Brand 요약
     * - Product 목록 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandSummary {
        private Long id;
        private String name;
    }

    /**
     * Brand 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandDetail {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private String website;
    }

    /**
     * ProductDetail 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Double weight;
        private Map<String, Object> dimensions; // JSON: {"width": float, "height": float, "depth": float}
        private String materials;
        private String countryOfOrigin;
        private String warrantyInfo;
        private String careInstructions;
        private Map<String, Object> additionalInfo; // JSON object for additional information
    }

    /**
     * ProductPrice 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Price {
        private BigDecimal basePrice;
        private BigDecimal salePrice;
        private BigDecimal costPrice;
        private String currency;
        private BigDecimal taxRate;
        private Integer discountPercentage = 0;
    }

    /**
     * Category 요약
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySummary {
        private Long id;
        private String name;
        private String slug;
        private ParentCategory parent;
    }

    /**
     * 상위 Category 요약
     * - Category 요약
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParentCategory {
        private Long id;
        private String name;
        private String slug;
    }

    /**
     * ProductOptionGroup 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionGroup {
        private Long id;
        private String name;
        private Integer displayOrder;
        private List<Option> options;
    }

    /**
     * ProductOption 상세
     * - ProductOptionGroup 상세
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private Long id;
        private Long optionGroupId; // 명시적으로 추가
        private String name;
        private BigDecimal additionalPrice;
        private String sku;
        private Integer stock;
        private Integer displayOrder;
    }

    /**
     * ProductImage 상세
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDetail {
        private Long id;
        private String url;
        private String altText;
        private boolean isPrimary;
        private Integer displayOrder;
        private Long optionId;
    }

    /**
     * ProductImage 요약
     * - Product 목록 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageSummary {
        private String url;
        private String altText;
    }

    /**
     * Tag 요약
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Tag {
        private Long id;
        private String name;
        private String slug;
    }

    /**
     * Rating 요약
     * - Product 상세 조회
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatingSummary {
        private Double average;
        private Integer count;
        private Map<Integer, Integer> distribution; // Map rating -> count (e.g., {5: 95, 4: 20, ...})
    }

    /**
     * Category 필수 필드 조회
     * - ProductCommand Create
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryBasic {
        private Long categoryId;
        private boolean isPrimary;
    }

    /**
     * Product 필수 필드 조회
     * - ProductCommand Create
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductBasic {
        private Long id;
        private String name;
        private String slug;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
