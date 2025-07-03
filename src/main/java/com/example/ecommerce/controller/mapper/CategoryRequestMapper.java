package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.query.CategoryQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryRequestMapper {
    CategoryQuery.ListCategory toListCategory(Integer level);

    @Mapping(source = "includeSubcategories", target = "includeSubCategories")
    CategoryQuery.CategoryProducts toCategoryProducts(Long categoryId, boolean includeSubcategories, PaginationDto.PaginationRequest request);
}
