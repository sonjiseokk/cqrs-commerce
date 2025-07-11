package com.example.ecommerce.service.query.nosql.repository;

import com.example.ecommerce.service.query.nosql.entity.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDocumentRepository extends MongoRepository<ProductDocument, Long> {
}