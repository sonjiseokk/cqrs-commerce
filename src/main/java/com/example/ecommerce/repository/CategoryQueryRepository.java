package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.service.dto.CategoryDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.ecommerce.entity.QCategory.category;
import static com.example.ecommerce.entity.QProductCategory.productCategory;

@Repository
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CategoryDto.FeaturedCategory> getFeaturedCategories() {
        List<Tuple> fetch = queryFactory.select(
                        productCategory.category.id,
                        productCategory.product.id.count()
                )
                .from(productCategory)
                .groupBy(productCategory.category.id)
                .orderBy(productCategory.product.id.count().desc(), productCategory.category.id.desc())
                .limit(5)
                .fetch();

        Map<Long, Long> productCount = new LinkedHashMap<>();
        fetch.forEach(t -> productCount.put(
                t.get(productCategory.category.id),       // key
                t.get(productCategory.product.id.count()) // value
        ));

        List<Category> categories = queryFactory
                .selectFrom(category)
                .where(category.id.in(productCount.keySet()))
                .fetch();


        return categories.stream()
                .map(cat -> new CategoryDto.FeaturedCategory(
                        cat.getId(),
                        cat.getName(),
                        cat.getSlug(),
                        cat.getImageUrl(),
                        productCount.get(cat.getId())
                ))
                .sorted(Comparator.comparing(CategoryDto.FeaturedCategory::getProductCount).reversed())
                .toList();
    }

}
