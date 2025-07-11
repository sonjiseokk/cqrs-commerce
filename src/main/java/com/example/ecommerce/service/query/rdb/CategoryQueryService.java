package com.example.ecommerce.service.query.rdb;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductQueryRepository;
import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.CategoryMapper;
import com.example.ecommerce.service.mapper.ProductMapper;
import com.example.ecommerce.service.query.CategoryQuery;
import com.example.ecommerce.service.query.CategoryQueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryHandler {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductQueryRepository productQueryRepository;
    private final ProductMapper productMapper;

    /**
     * 모든 카테고리를 조회하는 API 메소드
     * @param query
     * @return
     */
    @Override
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

    @Override
    public ProductResponse.CategoryProducts getCategoryProducts(CategoryQuery.CategoryProducts query) {
        // 최상위 카테고리 존재 확인
        Category parentCategory = categoryRepository.findByIdWithParent(query.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category", query.getCategoryId()));
        // 최상위 카테고리 정보 DTO 변환
        // category에 사용
        CategoryDto.Detail categoryDetail = categoryMapper.toCategoryDetail(parentCategory);

        // 서브 카테고리를 포함시키는 경우
        if (query.isIncludeSubCategories()) {
            // 전체 카테고리 flat 조회
            List<Category> allCategories = categoryRepository.findAll();

            // Map<부모 ID, 자식 리스트> 구성
            Map<Long, List<Category>> childrenMap = buildChildrenMap(allCategories);

            // 부모 + 자식 카테고리의 모든 카테고리 ID 조회
            List<Long> collectedIds = collectSubCategoryIds(parentCategory, childrenMap);

            // 카테고리 ID에 속하는 모든 Product 조회 (DTO Projection)
            Page<ProductDto.ProductSummary> result = productQueryRepository.getCategoriesProducts(collectedIds, query);

            // 페이징 정보 Build
            PaginationDto.PaginationInfo paginationInfo = PaginationDto.PaginationInfo.builder()
                    .totalItems((int) result.getTotalElements())
                    .totalPages((int) result.getTotalPages())
                    .currentPage(result.getNumber())
                    .perPage(result.getSize())
                    .build();

            return ProductResponse.CategoryProducts.builder()
                    .category(categoryDetail)
                    .items(result.getContent())
                    .pagination(paginationInfo)
                    .build();
        }
        return null;
    }

    //-----------------------------------------Helper Method----------------------------------------------

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

    private List<Long> collectSubCategoryIds(Category parentCategory, Map<Long, List<Category>> childrenMap) {
        // 자식 카테고리 조회
        List<Category> childrenList = childrenMap.getOrDefault(parentCategory.getId(), new ArrayList<>());

        // 부모 카테고리 추가
        childrenList.add(parentCategory);

        return childrenList.stream()
                .map(Category::getId)
                .toList();
    }
}
