package com.example.ecommerce.service.query.nosql.operation;

import com.example.ecommerce.service.query.nosql.entity.ProductDocument;

public interface ProductDocumentOperations {
    ProductDocument findProductDocumentWithReferences(Long productId);

}
