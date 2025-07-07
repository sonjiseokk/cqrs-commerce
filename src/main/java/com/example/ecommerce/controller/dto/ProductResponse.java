package com.example.ecommerce.controller.dto;

import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductResponse {
    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GetProduct {
        private ProductDto.ProductDetail data;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GetProductList {
        private List<ProductDto.ProductSummary> items;
        private PaginationDto.PaginationInfo pagination;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryProducts {
        private CategoryDto.Detail category;
        private List<ProductDto.ProductSummary> items;
        private PaginationDto.PaginationInfo pagination;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainPage {
        private List<ProductDto.ProductSummary> newProducts;
        private List<ProductDto.ProductSummary> popularProducts;
        private List<CategoryDto.FeaturedCategory> featuredCategories;
    }

}
