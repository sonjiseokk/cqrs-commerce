package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.service.dto.ProductDto;

import java.util.List;

public interface ProductQueryHandler {
    ProductResponse.GetProductList getProducts(ProductQuery.ListProducts query);

    ProductDto.ProductDetail getProduct(ProductQuery.GetProduct query);

    List<ProductDto.ProductSummary> getNewProducts();

    List<ProductDto.ProductSummary> getPopularProducts();
}
