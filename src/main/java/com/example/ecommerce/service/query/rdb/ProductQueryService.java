package com.example.ecommerce.service.query.rdb;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductQueryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductMapper;
import com.example.ecommerce.service.query.ProductQuery;
import com.example.ecommerce.service.query.ProductQueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Primary
public class ProductQueryService implements ProductQueryHandler {
    private final ProductRepository productRepository;
    private final ProductQueryRepository queryRepository;
    private final ProductMapper productMapper;
    private final ProductQueryRepository productQueryRepository;

    @Override
    public ProductResponse.GetProductList getProducts(ProductQuery.ListProducts query) {
        Page<ProductDto.ProductSummary> result = queryRepository.search(query);

        PaginationDto.PaginationInfo paginationInfo = PaginationDto.PaginationInfo.builder()
                .totalItems((int) result.getTotalElements())
                .totalPages((int) result.getTotalPages())
                .currentPage(result.getNumber())
                .perPage(result.getSize())
                .build();

        return ProductResponse.GetProductList.builder()
                .items(result.getContent())
                .pagination(paginationInfo)
                .build();
    }

    @Override
    public ProductDto.ProductDetail getProduct(ProductQuery.GetProduct query) {
        // 1. 다중 조인 단건 조회
        Product product = productQueryRepository.getProduct(query)
                .orElseThrow(() -> new ResourceNotFoundException("Product", query.getProductId()));

        // ProductDetail DTO 변환
        ProductDto.ProductDetail content = productMapper.toProductDetailDto(product);

        // 2. 관련 상품 리스트 DTO Projection 조회
        List<ProductDto.RelatedProduct> relatedProducts = productQueryRepository.getRelatedProduct(product);

        // 3. 관련 상품 리스트 Setter 주입
        content.setRelatedProducts(relatedProducts);

        return content;
    }

    @Override
    public List<ProductDto.ProductSummary> getNewProducts() {
        return productQueryRepository.getNewProducts();
    }

    @Override
    public List<ProductDto.ProductSummary> getPopularProducts() {
        return productQueryRepository.getPopularProducts();
    }
}
