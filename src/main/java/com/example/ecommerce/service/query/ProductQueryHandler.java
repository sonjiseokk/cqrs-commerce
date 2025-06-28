package com.example.ecommerce.service.query;

import com.example.ecommerce.controller.dto.ProductGetResponse;
import com.example.ecommerce.controller.dto.ProductListResponse;

public interface ProductQueryHandler {
    ProductListResponse getProducts(ProductQuery.ListProducts query);

    ProductGetResponse getProduct(ProductQuery.GetProduct query);
}
