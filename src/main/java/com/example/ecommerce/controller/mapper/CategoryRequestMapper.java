package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.service.query.CategoryQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryRequestMapper {
    CategoryQuery.ListCategory toListCategory(Integer level);
}
