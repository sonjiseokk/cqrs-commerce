package com.example.ecommerce.controller.dto;

import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainPageResponse {
    private List<ProductDto.ProductSummary> newProducts;
    private List<ProductDto.ProductSummary> popularProducts;
    private List<CategoryDto.FeaturedCategory> featuredCategories;
}
