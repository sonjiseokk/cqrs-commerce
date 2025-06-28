package com.example.ecommerce.service.query;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.controller.dto.ProductGetResponse;
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

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService implements ProductQueryHandler {
    private final ProductRepository productRepository;
    private final ProductQueryRepository queryRepository;
    private final ProductMapper productMapper;
    private final ProductQueryRepository productQueryRepository;

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

    @Override
    public ProductGetResponse getProduct(ProductQuery.GetProduct query) {
        // 1. 다중 조인 단건 조회
        Product product = productQueryRepository.getProduct(query)
                .orElseThrow(() -> new ResourceNotFoundException("getProduct", query.getProductId()));

        // ProductDetail DTO 변환
        ProductDto.ProductDetail content = productMapper.toProductDetailDto(product);

        // 2. 관련 상품 리스트 DTO Projection 조회
        List<ProductDto.RelatedProduct> relatedProducts = productQueryRepository.getRelatedProduct(product);

        // 3. 관련 상품 리스트 Setter 주입
        content.setRelatedProducts(relatedProducts);

        return new ProductGetResponse(content);
    }

}
