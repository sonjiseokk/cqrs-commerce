package com.example.ecommerce.service.mapper;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.service.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CategoryMapper {
    @Mapping(target = "children", ignore = true)
    CategoryDto.Category toCategoryDto(Category category);

    @Mapping(source = "parent", target = "parent")
    CategoryDto.Detail toCategoryDetail(Category category);

    CategoryDto.ParentCategory toParentCategory(Category category);
}
