package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ProductDto;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductCommand {
    /**
     * 공통 인터페이스 - 생성/수정 공용 필드
     */
    public interface ProductBase {
        String getName();
        String getSlug();
        String getShortDescription();
        String getFullDescription();
        Long getSellerId();
        Long getBrandId();
        String getStatus();
        ProductDto.Detail getDetail();
        ProductDto.Price getPrice();
        List<ProductDto.CategoryBasic> getCategories();
        List<ProductDto.OptionGroup> getOptionGroups();
        List<ProductDto.ImageDetail> getImages();
        List<Long> getTags();
    }

    /**
     * Product 생성 Command
     */
    @Data
    @Builder
    public static class CreateProduct implements ProductBase {
        private String name;
        private String slug;
        private String shortDescription;
        private String fullDescription;
        // 단일 ID
        private Long sellerId;
        private Long brandId;

        private String status;

        private ProductDto.Detail detail;
        private ProductDto.Price price;

        @Builder.Default
        private List<ProductDto.CategoryBasic> categories = new ArrayList<>();
        @Builder.Default
        private List<ProductDto.OptionGroup> optionGroups = new ArrayList<>();
        @Builder.Default
        private List<ProductDto.ImageDetail> images = new ArrayList<>();
        @Builder.Default
        private List<Long> tags = new ArrayList<>();
    }

    // Product 업데이트 Command
    @Data
    @Builder
    public static class UpdateProduct implements ProductBase {
        private Long productId;
        private String name;
        private String slug;
        private String shortDescription;
        private String fullDescription;
        // 단일 ID
        private Long sellerId;
        private Long brandId;

        private String status;

        private ProductDto.Detail detail;
        private ProductDto.Price price;

        @Builder.Default
        private List<ProductDto.CategoryBasic> categories = new ArrayList<>();
        @Builder.Default
        private List<ProductDto.OptionGroup> optionGroups = new ArrayList<>();
        @Builder.Default
        private List<ProductDto.ImageDetail> images = new ArrayList<>();
        @Builder.Default
        private List<Long> tags = new ArrayList<>();
    }

    @Data
    @Builder
    public static class DeleteProduct {
        private Long productId;
    }

    @Data
    @Builder
    public static class UpdateOption {
        private Long productId;
        private Long optionId;
        private String name;
        private BigDecimal additionalPrice;
        private String sku;
        private Integer stock;
        private Integer displayOrder;
    }

    @Data
    @Builder
    public static class DeleteOption {
        private Long productId;
        private Long optionId;
    }

    @Data
    @Builder
    public static class CreateImage {
        private Long productId;
        private String url;
        private String altText;
        private boolean isPrimary;
        private Integer displayOrder;
        private Long optionId;
    }

    @Data
    @Builder
    public static class DeleteImage {
        private Long productId;
        private Long imageId;
    }
}
