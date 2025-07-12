package com.example.ecommerce.service.query.nosql.service;

import com.example.ecommerce.service.query.ProductQuery;
import com.example.ecommerce.service.query.nosql.entity.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSearchOperationsImpl implements ProductSearchOperations {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ProductDocument> searchProductsByConditions(ProductQuery.ListProducts command) {
        Pageable pageable = command.getPagination().toPageable();

        List<Criteria> criteriaList = new ArrayList<>();

        if (command.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(command.getStatus().toUpperCase()));
        }

        if (command.getMinPrice() != null) {
            criteriaList.add(Criteria.where("basePrice").gte(command.getMinPrice()));
        }

        if (command.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("basePrice").lte(command.getMaxPrice()));
        }

        if (command.getCategory() != null && !command.getCategory().isEmpty()) {
            criteriaList.add(Criteria.where("categoryIds").in(command.getCategory()));
        }

        if (command.getSeller() != null) {
            criteriaList.add(Criteria.where("sellerId").is(command.getSeller()));
        }

        if (command.getBrand() != null) {
            criteriaList.add(Criteria.where("brandId").is(command.getBrand()));
        }

        if (command.getInStock() != null) {
            criteriaList.add(Criteria.where("inStock").is(command.getInStock()));
        }

        if (command.getSearch() != null && !command.getSearch().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(command.getSearch(), "i"),
                    Criteria.where("shortDescription").regex(command.getSearch(), "i"),
                    Criteria.where("fullDescription").regex(command.getSearch(), "i"),
                    Criteria.where("materials").regex(command.getSearch(), "i")
            );
            criteriaList.add(searchCriteria);
        }

        if (command.getCreatedFrom() != null) {
            criteriaList.add(Criteria.where("createdAt").gte(command.getCreatedFrom()));
        }

        if (command.getCreatedTo() != null) {
            criteriaList.add(Criteria.where("createdAt").lte(command.getCreatedTo()));
        }

        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(finalCriteria).with(pageable);

        log.debug("MongoDB query: {}", query);

        long totalItems = mongoTemplate.count(query.skip(0).limit(0), ProductDocument.class, "products");

        List<ProductDocument> content = mongoTemplate.find(query, ProductDocument.class, "products");

        return new PageImpl<>(content, pageable, totalItems);
    }
}

