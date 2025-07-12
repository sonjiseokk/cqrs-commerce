package com.example.ecommerce.service.query.nosql.service;

import com.example.ecommerce.service.query.ProductQuery;
import com.example.ecommerce.service.query.nosql.entity.ProductDocument;
import com.example.ecommerce.service.query.nosql.entity.ProductSearchDocument;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSearchOperations {

    Page<ProductDocument> searchProductsByConditions(ProductQuery.ListProducts query);
}
