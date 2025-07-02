package com.example.ecommerce.service.query;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto.Category> getAllCategories(CategoryQuery.ListCategory query) {
        // 전체 카테고리 flat 조회
        List<Category> allCategories = categoryRepository.findAll();

        // Map<부모 ID, 자식 리스트> 구성
        Map<Long, List<Category>> childrenMap = buildChildrenMap(allCategories);

        // root 카테고리(level) 필터링
        int targetLevel = (query != null && query.getLevel() != null)
                ? query.getLevel()
                : 1; // 기본값: 최상위

        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getLevel() == targetLevel)
                .toList();

        // 트리 구조 구성
        return rootCategories.stream()
                .map(root -> buildCategoryTree(root, childrenMap))
                .toList();
    }

    // 전체 카테고리를 Map 형태로 구성
    private Map<Long, List<Category>> buildChildrenMap(List<Category> categories) {
        Map<Long, List<Category>> map = new HashMap<>();
        for (Category category : categories) {
            if (category.getParent() != null) {
                Long parentId = category.getParent().getId();
                map.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
            }
        }
        return map;
    }

    // 재귀적으로 부모 카테고리 -> 자식 카테고리를 구성
    private CategoryDto.Category buildCategoryTree(Category category, Map<Long, List<Category>> categoryMap) {
        // 1. 현재 카테고리를 DTO로 변환
        CategoryDto.Category categoryDto = categoryMapper.toCategoryDto(category);

        // 2. 현재 카테고리의 자식 리스트
        List<Category> child = categoryMap.getOrDefault(category.getId(), new ArrayList<>());
        if (!child.isEmpty()) {
            // 자식이 있는 경우 재귀적으로 모두 DTO로 변환
            List<CategoryDto.Category> childDto = child.stream()
                    .map(childCategory -> buildCategoryTree(childCategory, categoryMap))
                    .toList();
            categoryDto.setChildren(childDto);
        }

        return categoryDto;
    }
}
