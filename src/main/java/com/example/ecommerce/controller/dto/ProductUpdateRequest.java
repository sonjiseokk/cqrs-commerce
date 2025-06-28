package com.example.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductUpdateRequest {
    @NotNull
    @Length(min = 1, max = 255)
    private String name;

    @NotNull
    @Length(min = 1, max = 255)
    private String slug;

    @Length(max = 500)
    private String shortDescription;
    private String fullDescription;

    @NotNull
    private Long sellerId;

    @NotNull
    private Long brandId;
    private String status; // ACTIVE, OUT_OF_STOCK, DELETED

    private ProductCreateRequest.ProductDetailDto detail;
    private ProductCreateRequest.ProductPriceDto price;
    @Builder.Default
    private List<ProductCreateRequest.ProductCategoryDto> categories = new ArrayList<>();
    @Builder.Default
    private List<ProductCreateRequest.OptionGroupDto> optionGroups = new ArrayList<>();
    @Builder.Default
    private List<ProductCreateRequest.ImageDto> images = new ArrayList<>();
    @Builder.Default
    private List<Long> tagIds = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductDetailDto {
        private Double weight;
        private Map<String, Object> dimensions;
        private String materials;
        private String countryOfOrigin;
        private String warrantyInfo;
        private String careInstructions;
        private Map<String, Object> additionalInfo;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductPriceDto {
        private BigDecimal basePrice;
        private BigDecimal salePrice;
        private BigDecimal costPrice;
        private String currency;
        private BigDecimal taxRate;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductCategoryDto {
        private Long categoryId;
        @Builder.Default
        private Boolean isPrimary = false;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionGroupDto {
        private String name;
        private Integer displayOrder;
        @Builder.Default
        private List<ProductCreateRequest.OptionDto> options = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionDto {
        private String name;
        private BigDecimal additionalPrice;
        private String sku;
        private Integer stock;
        private Integer displayOrder;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageDto {
        private String url;
        private String altText;
        private boolean isPrimary;
        private Integer displayOrder;
        private Long optionId;
    }
}
