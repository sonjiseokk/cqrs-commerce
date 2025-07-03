package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.CategoryGetProducts;
import com.example.ecommerce.service.dto.CategoryDto;

import java.util.List;

public interface CategoryQueryHandler {
    List<CategoryDto.Category> getAllCategories(CategoryQuery.ListCategory query);

    CategoryGetProducts getCategoryProducts(CategoryQuery.CategoryProducts query);
}
