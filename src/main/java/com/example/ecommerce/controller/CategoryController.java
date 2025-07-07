package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.controller.mapper.CategoryRequestMapper;
import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.query.CategoryQuery;
import com.example.ecommerce.service.query.CategoryQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryQueryHandler categoryQueryHandler;
    private final CategoryRequestMapper categoryRequestMapper;

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getAllCategories(@RequestParam(required = false) Integer level) {
        CategoryQuery.ListCategory query = categoryRequestMapper.toListCategory(level);
        List<CategoryDto.Category> response = categoryQueryHandler.getAllCategories(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "카테고리 목록을 성공적으로 조회했습니다."
        ));
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<ApiResponse<?>> getProductsByCategoryId(@PathVariable Long categoryId,
                                                                  @RequestParam(required = false, defaultValue = "true") Boolean includeSubcategories,
                                                                  @ModelAttribute PaginationDto.PaginationRequest request) {
        CategoryQuery.CategoryProducts query = categoryRequestMapper.toCategoryProducts(categoryId, includeSubcategories, request);

        ProductResponse.CategoryProducts response = categoryQueryHandler.getCategoryProducts(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "카테고리 상품 목록을 성공적으로 조회했습니다."
        ));
    }
}
