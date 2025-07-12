package com.example.ecommerce.service.query.nosql.service;


import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductNoSqlMapper;
import com.example.ecommerce.service.query.ProductQuery;
import com.example.ecommerce.service.query.ProductQueryHandler;
import com.example.ecommerce.service.query.nosql.entity.BrandDocument;
import com.example.ecommerce.service.query.nosql.entity.ProductDocument;
import com.example.ecommerce.service.query.nosql.entity.SellerDocument;
import com.example.ecommerce.service.query.nosql.operation.ProductDocumentOperations;
import com.example.ecommerce.service.query.nosql.repository.BrandDocumentRepository;
import com.example.ecommerce.service.query.nosql.repository.ProductDocumentRepository;
import com.example.ecommerce.service.query.nosql.repository.SellerDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductNoSqlQueryService implements ProductQueryHandler {
    private final ProductDocumentOperations productDocumentOperations;
    private final SellerDocumentRepository sellerDocumentRepository;
    private final ProductNoSqlMapper productNoSqlMapper;
    private final BrandDocumentRepository brandDocumentRepository;
    private final ProductSearchOperations productSearchOperations;

    @Override
    public ProductResponse.GetProductList getProducts(ProductQuery.ListProducts query) {
        Page<ProductDocument> productDocuments = productSearchOperations.searchProductsByConditions(query);

        List<ProductDto.ProductSummary> productSummaries = productDocuments.stream()
                .map(productNoSqlMapper::toProductSummaryDto)
                .toList();

        long totalItems = productDocuments.getTotalElements();
        int totalPages = productDocuments.getTotalPages();
        int currentPage = productDocuments.getNumber() + 1;
        int perPage = productDocuments.getNumberOfElements();

        PaginationDto.PaginationInfo paginationInfo = PaginationDto.PaginationInfo.builder()
                .totalItems((int) totalItems)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .perPage(perPage)
                .build();

        return ProductResponse.GetProductList.builder()
                .items(productSummaries)
                .pagination(paginationInfo)
                .build();
    }

    @Override
    public ProductDto.ProductDetail getProduct(ProductQuery.GetProduct query) {
        Long productId = query.getProductId();

        // MongoDB에서 상품 정보 조회 (Aggregation을 이용한 단일 쿼리로 조인)
        ProductDocument productDocument = productDocumentOperations.findProductDocumentWithReferences(productId);

        // 없는 경우 예외 처리
        if (productDocument == null) {
            throw new ResourceNotFoundException("product", productId);
        }

        // MongoDB에서 Seller 정보 조회
        SellerDocument sellerDocument = sellerDocumentRepository.findById(productDocument.getSeller().getId()).orElseThrow(
                () -> new ResourceNotFoundException("SellerDocument", productDocument.getSeller().getId())
        );

        // MongoDB에서 Brand 정보 조회
        BrandDocument brandDocument = brandDocumentRepository.findById(productDocument.getBrand().getId()).orElseThrow(
                () -> new ResourceNotFoundException("SellerDocument", productDocument.getBrand().getId())
        );

        return productNoSqlMapper.toProductDetailDto(productDocument, sellerDocument, brandDocument);

    }

    @Override
    public List<ProductDto.ProductSummary> getNewProducts() {
        return List.of();
    }

    @Override
    public List<ProductDto.ProductSummary> getPopularProducts() {
        return List.of();
    }
}
