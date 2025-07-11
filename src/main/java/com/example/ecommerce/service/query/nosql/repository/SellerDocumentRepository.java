package com.example.ecommerce.service.query.nosql.repository;

import com.example.ecommerce.service.query.nosql.entity.SellerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerDocumentRepository extends MongoRepository<SellerDocument, Long> {
}