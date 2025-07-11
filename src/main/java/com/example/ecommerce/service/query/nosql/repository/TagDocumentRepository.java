package com.example.ecommerce.service.query.nosql.repository;

import com.example.ecommerce.service.query.nosql.entity.TagDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDocumentRepository extends MongoRepository<TagDocument, Long> {
}