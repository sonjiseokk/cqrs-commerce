package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.ProductListResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductQueryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService implements ProductQueryHandler {
    private final ProductRepository productRepository;
    private final ProductQueryRepository queryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductListResponse getProducts(ProductQuery.ListProducts query) {
        Page<Product> result = queryRepository.search(query);

        List<ProductDto.ProductSummary> content = result.stream()
                .map(productMapper::toProductSummaryDto)
                .toList();

        PaginationDto.PaginationInfo paginationInfo = PaginationDto.PaginationInfo.builder()
                .totalItems((int) result.getTotalElements())
                .totalPages((int) result.getTotalPages())
                .currentPage(result.getNumber())
                .perPage(result.getSize())
                .build();

        return ProductListResponse.builder()
                .items(content)
                .pagination(paginationInfo)
                .build();
    }
}
