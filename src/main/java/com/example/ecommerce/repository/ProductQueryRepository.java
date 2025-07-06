package com.example.ecommerce.repository;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.query.CategoryQuery;
import com.example.ecommerce.service.query.ProductQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.ecommerce.entity.QBrand.brand;
import static com.example.ecommerce.entity.QCategory.category;
import static com.example.ecommerce.entity.QProduct.product;
import static com.example.ecommerce.entity.QProductCategory.productCategory;
import static com.example.ecommerce.entity.QProductDetail.productDetail;
import static com.example.ecommerce.entity.QProductImage.productImage;
import static com.example.ecommerce.entity.QProductOption.productOption;
import static com.example.ecommerce.entity.QProductOptionGroup.productOptionGroup;
import static com.example.ecommerce.entity.QProductPrice.productPrice;
import static com.example.ecommerce.entity.QProductTag.productTag;
import static com.example.ecommerce.entity.QReview.review;
import static com.example.ecommerce.entity.QSeller.seller;
import static com.example.ecommerce.entity.QTag.tag;
import static com.querydsl.jpa.JPAExpressions.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ProductQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Product> getProduct(ProductQuery.GetProduct query) {
        // 1차 조회 - product
        Product content = queryFactory.select(product)
                .from(product)
                .leftJoin(product.price, productPrice).fetchJoin()
                .leftJoin(product.detail, productDetail).fetchJoin()
                .leftJoin(product.seller, seller).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
                .where(product.id.eq(query.getProductId()))
                .where(product.status.ne(ProductStatus.DELETED))
                .fetchOne();

        // 제품 없는 경우
        if (content == null) {
            return Optional.empty();
        }

        // 2차 조회 - optionGroup
        List<ProductOptionGroup> optionGroups = queryFactory
                .selectDistinct(productOptionGroup)
                .from(productOptionGroup)
                .where(productOptionGroup.product.id.eq(query.getProductId()))
                .fetch();

        // 3차 조회 - options
        List<ProductOption> options = queryFactory
                .selectFrom(QProductOption.productOption)
                .where(QProductOption.productOption.optionGroup.id.in(
                        optionGroups.stream().map(ProductOptionGroup::getId).toList()
                ))
                .fetch();

        // 1. 옵션 리스트를 groupId 기준으로 map으로 묶고
        Map<Long, List<ProductOption>> optionsByGroupId = options.stream()
                .collect(Collectors.groupingBy(option -> option.getOptionGroup().getId()));

        // 2. 각 그룹에 직접 세팅

        for (ProductOptionGroup group : optionGroups) {
            group.getOptions().clear();
            List<ProductOption> relatedOptions = optionsByGroupId.getOrDefault(group.getId(), List.of());
            group.getOptions().addAll(relatedOptions); // 수동으로 options 연결
        }

        // 4차 조회 - tags
        List<ProductTag> tags = queryFactory.select(productTag)
                .from(productTag)
                .leftJoin(productTag.tag, tag).fetchJoin()
                .where(productTag.product.id.eq(query.getProductId()))
                .fetch();

        QCategory parentCategory = new QCategory("parentCategory");

        // 5차 조회 - categories
        List<ProductCategory> categories = queryFactory.select(productCategory)
                .from(productCategory)
                .leftJoin(productCategory.category, category).fetchJoin()
                .leftJoin(category.parent, parentCategory).fetchJoin()
                .where(productCategory.product.id.eq(query.getProductId()))
                .fetch();

        for (ProductCategory entity : categories) {
            Hibernate.initialize(entity.getCategory());
        }

        // LAZY -> EAGER
        content.eagerLoad(optionGroups, tags, categories);

        return Optional.ofNullable(content);
    }

    // 관련 제품 리스트 조회
    // - 1번째 카테고리
    // - 랜덤 정렬된 5가지 제품
    public List<ProductDto.RelatedProduct> getRelatedProduct(Product content) {
        // 중간 엔티티 -> Category 연결
        ProductCategory selectedProductCategory = content.getCategories().get(0);
        Category selectedCategory = selectedProductCategory.getCategory();

        // flat DTO Projection 조회
        List<Tuple> result = queryFactory
                .select(
                        product.id,
                        product.name,
                        product.slug,
                        product.shortDescription,
                        productPrice.basePrice,
                        productPrice.salePrice,
                        productPrice.currency,
                        productImage.url,
                        productImage.altText
                )
                .from(product)
                .leftJoin(product.price, productPrice)
                .leftJoin(product.images, productImage)
                .leftJoin(product.categories, productCategory)
                .where(
                        productCategory.category.id.eq(selectedCategory.getId()),
                        product.id.ne(content.getId())
                )
                .orderBy(Expressions.numberTemplate(Double.class, "random()").asc())
                .limit(5)
                .fetch();

        // DTO 변환
        return result.stream()
                .map(tuple -> ProductDto.RelatedProduct.builder()
                        .id(tuple.get(product.id))
                        .name(tuple.get(product.name))
                        .slug(tuple.get(product.slug))
                        .shortDescription(tuple.get(product.shortDescription))
                        .basePrice(tuple.get(productPrice.basePrice))
                        .salePrice(tuple.get(productPrice.salePrice))
                        .currency(tuple.get(productPrice.currency))
                        .primaryImage(ProductDto.ImageSummary.builder()
                                .url(tuple.get(productImage.url))
                                .altText(tuple.get(productImage.altText))
                                .build())
                        .build())
                .toList();
    }

    // 여러 필터 적용된 검색 기능 + 페이징
    public Page<Product> search(ProductQuery.ListProducts query) {
        // 페이징 정보 추출
        Pageable pageable = query.getPagination().toPageable();

        // 정렬 기준 추출
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());

        List<Product> content = queryFactory.selectDistinct(product)
                .from(product)
                .leftJoin(product.price, productPrice).fetchJoin()
                .leftJoin(product.detail, productDetail).fetchJoin()
                .leftJoin(product.seller, seller).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
//                .leftJoin(product.optionGroups, productOptionGroup).fetchJoin()
                .where(
                        statusEq(query.getStatus()),
                        priceGoe(query.getMinPrice()),
                        priceLoe(query.getMaxPrice()),
                        categoryIn(query.getCategory()),
                        sellerEq(query.getSeller()),
                        brandEq(query.getBrand()),
//                        inStockCond(query.getInStock()),
                        keywordLike(query.getSearch()),
                        createdAfter(query.getCreatedFrom()),
                        createdBefore(query.getCreatedTo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers)
                .fetch();

        long totalCount = queryFactory.select(product.count())
                .from(product)
                .leftJoin(product.price, productPrice)
                .leftJoin(product.detail, productDetail)
                .leftJoin(product.seller, seller)
                .leftJoin(product.brand, brand)
                .leftJoin(product.images, productImage)
//                .leftJoin(product.optionGroups, productOptionGroup).fetchJoin()
                .where(
                        statusEq(query.getStatus()),
                        priceGoe(query.getMinPrice()),
                        priceLoe(query.getMaxPrice()),
                        categoryIn(query.getCategory()),
                        sellerEq(query.getSeller()),
                        brandEq(query.getBrand()),
//                        inStockCond(query.getInStock()),
                        keywordLike(query.getSearch()),
                        createdAfter(query.getCreatedFrom()),
                        createdBefore(query.getCreatedTo())
                )
                .fetchOne();


        return new PageImpl<>(content, pageable, totalCount);
    }

    public Page<ProductDto.ProductSummary> getCategoriesProducts(List<Long> categoryIds, CategoryQuery.CategoryProducts query) {
        // Pageable 변환
        Pageable pageable = query.getRequest().toPageable();

        // 정렬 기준 추출
        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort());

        // 1. 특정 카테고리들에 속한 Product ID 리스트
        List<Long> productIds = queryFactory
                .select(product.id)
                .from(product)
                .join(productCategory).on(product.id.eq(productCategory.product.id))
                .where(productCategory.category.id.in(categoryIds))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. product 기본 조회 (price, detail, seller, brand)
        List<Product> products = queryFactory
                .selectFrom(product)
                .leftJoin(product.price, productPrice).fetchJoin()
                .leftJoin(product.detail, productDetail).fetchJoin()
                .leftJoin(product.seller, seller).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(product.images, productImage).fetchJoin()
                .where(product.id.in(productIds))
                .fetch();

        // 3. productIds 에 속한 모든 리뷰 flat 조회
        List<Review> reviews = queryFactory.selectFrom(review)
                .leftJoin(review.product, product)
                .where(review.product.id.in(productIds))
                .fetch();

        // 3-1. productId 기준으로 그룹화
        Map<Long, List<Review>> reviewMap = reviews.stream()
                .collect(Collectors.groupingBy(r -> r.getProduct().getId()));

        // 4. productIds에 속한 모든 이미지 flat 조회
        List<ProductImage> productImages = queryFactory
                .selectFrom(productImage)
                .where(productImage.product.id.in(productIds))
                .orderBy(productImage.displayOrder.asc())
                .fetch();

        // 4-1. productId 기준으로 그룹화
        // primary image 우선 탐색, 없다면 1번째 이미지
        Map<Long, ProductImage> imageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), imgs -> {
                            return imgs.stream()
                                    .filter(ProductImage::isPrimary) // isPrimary == true 인 경우
                                    .findFirst()
                                    .orElse(imgs.get(0)); // 없으면 displayOrder 기준 첫 번째
                        })
                ));


        // 5. 다단계 조회 + Map 묶기
        List<ProductDto.ProductSummary> content = products.stream()
                .map(p -> {
                    // 각 Product에 맞는 리뷰
                    List<Review> productReviews = reviewMap.getOrDefault(p.getId(), List.of());
                    double avgRating = productReviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
                    int reviewCount = productReviews.size();

                    // 각 프로덕트의 primary image
                    ProductImage image = imageMap.get(p.getId());

                    return ProductDto.ProductSummary.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .slug(p.getSlug())
                            .shortDescription(p.getShortDescription())
                            .basePrice(p.getPrice().getBasePrice())
                            .salePrice(p.getPrice().getSalePrice())
                            .currency(p.getPrice().getCurrency())
                            .primaryImage(image != null ? ProductDto.ImageSummary.builder()
                                    .url(image.getUrl())
                                    .altText(image.getAltText())
                                    .build() : null)
                            .brand(ProductDto.BrandSummary.builder()
                                    .id(p.getBrand().getId())
                                    .name(p.getBrand().getName())
                                    .build())
                            .seller(ProductDto.SellerSummary.builder()
                                    .id(p.getSeller().getId())
                                    .name(p.getSeller().getName())
                                    .build())
                            .rating(avgRating)
                            .reviewCount(reviewCount)
                            .inStock(p.getStatus() == ProductStatus.ACTIVE)
                            .status(p.getStatus().name())
                            .createdAt(p.getCreatedAt())
                            .build();
                })
                .toList();

        // 6. 카운트 쿼리
        Long total = queryFactory
                .select(product.countDistinct())
                .from(product)
                .join(productCategory).on(product.id.eq(productCategory.product.id))
                .where(productCategory.category.id.in(categoryIds))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    public List<ProductDto.ProductSummary> getNewProducts() {
        // 1. product 기본 조회 (price, detail, seller, brand)
        List<Product> products = queryFactory
                .selectFrom(product)
                .leftJoin(product.price, productPrice).fetchJoin()
                .leftJoin(product.detail, productDetail).fetchJoin()
                .leftJoin(product.seller, seller).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .orderBy(product.createdAt.desc(), product.id.desc())   // tie-break
                .limit(5)
                .fetch();

        // 2. ID 값만 추출
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 3. productIds 에 속한 모든 리뷰 flat 조회
        List<Review> reviews = queryFactory
                .selectFrom(review)
                .join(review.product, product)
                .where(product.id.in(productIds))
                .fetch();

        // 3-1. productId 기준으로 그룹화
        Map<Long, List<Review>> reviewMap = reviews.stream()
                .collect(Collectors.groupingBy(r -> r.getProduct().getId()));

        // 4. productIds에 속한 모든 이미지 flat 조회
        List<ProductImage> productImages = queryFactory
                .selectFrom(productImage)
                .where(productImage.product.id.in(productIds))
                .orderBy(productImage.displayOrder.asc())
                .fetch();

        // 4-1. productId 기준으로 그룹화
        // primary image 우선 탐색, 없다면 1번째 이미지
        Map<Long, ProductImage> imageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), imgs -> {
                            return imgs.stream()
                                    .filter(ProductImage::isPrimary) // isPrimary == true 인 경우
                                    .findFirst()
                                    .orElse(imgs.get(0)); // 없으면 displayOrder 기준 첫 번째
                        })
                ));

        // 5. 다단계 조회 + Map 묶기
        List<ProductDto.ProductSummary> content = products.stream()
                .map(p -> {
                    // 각 Product에 맞는 리뷰
                    List<Review> productReviews = reviewMap.getOrDefault(p.getId(), List.of());
                    double avgRating = productReviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
                    int reviewCount = productReviews.size();

                    // 각 프로덕트의 primary image
                    ProductImage image = imageMap.get(p.getId());

                    return ProductDto.ProductSummary.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .slug(p.getSlug())
                            .shortDescription(p.getShortDescription())
                            .basePrice(p.getPrice().getBasePrice())
                            .salePrice(p.getPrice().getSalePrice())
                            .currency(p.getPrice().getCurrency())
                            .primaryImage(image != null ? ProductDto.ImageSummary.builder()
                                    .url(image.getUrl())
                                    .altText(image.getAltText())
                                    .build() : null)
                            .brand(ProductDto.BrandSummary.builder()
                                    .id(p.getBrand().getId())
                                    .name(p.getBrand().getName())
                                    .build())
                            .seller(ProductDto.SellerSummary.builder()
                                    .id(p.getSeller().getId())
                                    .name(p.getSeller().getName())
                                    .build())
                            .rating(avgRating)
                            .reviewCount(reviewCount)
                            .inStock(p.getStatus() == ProductStatus.ACTIVE)
                            .status(p.getStatus().name())
                            .createdAt(p.getCreatedAt())
                            .build();
                })
                .toList();

        return content;
    }

    public List<ProductDto.ProductSummary> getPopularProducts() {
        // 1. 상위 5개의 리뷰 많은 productIds 조회
        List<Long> productIds = queryFactory
                .select(review.product.id)
                .from(review)
                .groupBy(review.product.id)
                .orderBy(review.count().desc())
                .limit(5)
                .fetch();

        // 리뷰가 없다면 예외처리
        if (productIds.isEmpty()) return List.of();

        // 2. 실제 Product 조회
        List<Product> products = queryFactory.select(product)
                .from(product)
                .where(product.id.in(productIds))
                .fetch();

        // 3. productIds 에 속한 모든 리뷰 flat 조회
        List<Review> reviews = queryFactory
                .selectFrom(review)
                .join(review.product, product)
                .where(product.id.in(productIds))
                .fetch();

        // 3-1. productId 기준으로 그룹화
        Map<Long, List<Review>> reviewMap = reviews.stream()
                .collect(Collectors.groupingBy(r -> r.getProduct().getId()));

        // 4. productIds에 속한 모든 이미지 flat 조회
        List<ProductImage> productImages = queryFactory
                .selectFrom(productImage)
                .where(productImage.product.id.in(productIds))
                .orderBy(productImage.displayOrder.asc())
                .fetch();

        // 4-1. productId 기준으로 그룹화
        // primary image 우선 탐색, 없다면 1번째 이미지
        Map<Long, ProductImage> imageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), imgs -> {
                            return imgs.stream()
                                    .filter(ProductImage::isPrimary) // isPrimary == true 인 경우
                                    .findFirst()
                                    .orElse(imgs.get(0)); // 없으면 displayOrder 기준 첫 번째
                        })
                ));

        // 5. 다단계 조회 + Map 묶기
        List<ProductDto.ProductSummary> content = products.stream()
                .map(p -> {
                    // 각 Product에 맞는 리뷰
                    List<Review> productReviews = reviewMap.getOrDefault(p.getId(), List.of());
                    double avgRating = productReviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
                    int reviewCount = productReviews.size();

                    // 각 프로덕트의 primary image
                    ProductImage image = imageMap.get(p.getId());

                    return ProductDto.ProductSummary.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .slug(p.getSlug())
                            .shortDescription(p.getShortDescription())
                            .basePrice(p.getPrice().getBasePrice())
                            .salePrice(p.getPrice().getSalePrice())
                            .currency(p.getPrice().getCurrency())
                            .primaryImage(image != null ? ProductDto.ImageSummary.builder()
                                    .url(image.getUrl())
                                    .altText(image.getAltText())
                                    .build() : null)
                            .brand(ProductDto.BrandSummary.builder()
                                    .id(p.getBrand().getId())
                                    .name(p.getBrand().getName())
                                    .build())
                            .seller(ProductDto.SellerSummary.builder()
                                    .id(p.getSeller().getId())
                                    .name(p.getSeller().getName())
                                    .build())
                            .rating(avgRating)
                            .reviewCount(reviewCount)
                            .inStock(p.getStatus() == ProductStatus.ACTIVE)
                            .status(p.getStatus().name())
                            .createdAt(p.getCreatedAt())
                            .build();
                })
                .toList();

        return content;
    }

    // -----------------------------------------------------

    private BooleanExpression statusEq(String status) {
        return status == null ? null : product.status.eq(ProductStatus.valueOf(status.toUpperCase()));
    }

    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice == null ? null : product.price.basePrice.goe(minPrice);
    }

    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice == null ? null : product.price.basePrice.loe(maxPrice);
    }

    private BooleanExpression categoryIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return product.categories.any().id.in(categoryIds);
    }

    private BooleanExpression sellerEq(Long sellerId) {
        return sellerId == null ? null : product.seller.id.eq(sellerId);
    }

    private BooleanExpression brandEq(Long brandId) {
        return brandId == null ? null : product.brand.id.eq(brandId);
    }

    private BooleanExpression inStockCond(Boolean inStock) {
        if (inStock == null) return null;

        if (inStock) {
            // 재고가 있는 상품: 옵션 중 하나라도 stock > 0
            return product.optionGroups.any().options.any().stock.gt(0);
        } else {
            // 서브쿼리: stock > 0인 옵션이 없는 경우
            JPQLQuery<Integer> subquery = selectOne()
                    .from(productOptionGroup)
                    .join(productOptionGroup.options, productOption)
                    .where(
                            productOptionGroup.product.eq(product),
                            productOption.stock.gt(0)
                    );

            return product.status.eq(ProductStatus.OUT_OF_STOCK)
                    .or(subquery.notExists());
        }
    }

    private BooleanExpression keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        String pattern = "%" + keyword + "%";

        // 이름 & 요약 & 설명에서 검색
        return product.name.lower().like(pattern)
                .or(product.shortDescription.lower().like(pattern))
                .or(product.fullDescription.upper().like(pattern));
    }

    // 시작일 이후
    private BooleanExpression createdAfter(LocalDate createdFrom) {
        if (createdFrom == null) return null;
        return product.createdAt.goe(createdFrom.atStartOfDay());
    }

    // 종료일 이전
    private BooleanExpression createdBefore(LocalDate createdTo) {
        if (createdTo == null) return null;
        return product.createdAt.loe(createdTo.plusDays(1).atStartOfDay().minusSeconds(1));
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "createdAt" -> orders.add(new OrderSpecifier<>(direction, product.createdAt));
                case "price" -> orders.add(new OrderSpecifier<>(direction, productPrice.basePrice));
                case "name" -> orders.add(new OrderSpecifier<>(direction, product.name));
                default -> throw new IllegalArgumentException("정렬 불가 필드: " + order.getProperty());
            }
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

}
