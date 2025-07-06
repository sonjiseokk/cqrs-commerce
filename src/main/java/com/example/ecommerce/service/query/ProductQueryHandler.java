package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.ProductGetResponse;
import com.example.ecommerce.controller.dto.ProductListResponse;
import com.example.ecommerce.service.dto.ProductDto;

import java.util.List;

public interface ProductQueryHandler {
    ProductListResponse getProducts(ProductQuery.ListProducts query);

    ProductGetResponse getProduct(ProductQuery.GetProduct query);

    List<ProductDto.ProductSummary> getNewProducts();

    List<ProductDto.ProductSummary> getPopularProducts();
}
