package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ProductDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ProductCommand {
    /** Product 생성 Command */
    @Data
    @Builder
    public static class CreateProduct {
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
}
