package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.MainPageResponse;
import com.example.ecommerce.repository.CategoryQueryRepository;
import com.example.ecommerce.repository.ProductQueryRepository;
import com.example.ecommerce.service.dto.CategoryDto;
import com.example.ecommerce.service.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MainQueryService {
    private final ProductQueryRepository productQueryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    @Transactional(readOnly = true)
    public MainPageResponse main() {
        List<ProductDto.ProductSummary> newProducts = productQueryRepository.getNewProducts();

        List<ProductDto.ProductSummary> popularProducts = productQueryRepository.getPopularProducts();

        List<CategoryDto.FeaturedCategory> featuredCategories = categoryQueryRepository.getFeaturedCategories();

        return MainPageResponse.builder()
                .newProducts(newProducts)
                .popularProducts(popularProducts)
                .featuredCategories(featuredCategories)
                .build();
    }
}
