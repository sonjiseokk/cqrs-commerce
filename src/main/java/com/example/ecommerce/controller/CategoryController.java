package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.mapper.CategoryRequestMapper;
import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.mapper.CategoryMapper;
import com.example.ecommerce.service.query.CategoryQuery;
import com.example.ecommerce.service.query.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryRequestMapper categoryRequestMapper;

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getAllCategories(@RequestParam(required = false) Integer level) {
        CategoryQuery.ListCategory query = categoryRequestMapper.toListCategory(level);
        List<CategoryDto.Category> response = categoryService.getAllCategories(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "카테고리 목록을 성공적으로 조회했습니다."
        ));
    }
}
