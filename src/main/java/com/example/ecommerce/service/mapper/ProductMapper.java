package com.example.ecommerce.service.mapper;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.service.command.ProductCommand;
import com.example.ecommerce.service.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductMapper {
    // -------------------------------------------------------------------------
    // Command To Entity
    // -------------------------------------------------------------------------

    // 기본 설정
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // 연관 관계는 서비스에서 주입 → 모두 무시
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "detail", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "optionGroups", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Product toProductEntity(ProductCommand.CreateProduct command);

    // ProductDetail 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    ProductDetail toProductDetailEntity(ProductDto.Detail dto,
                                        @Context Product product);

    // ProductPrice 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    ProductPrice toProductPriceEntity(ProductDto.Price dto,
                                      @Context Product product);

    // ProductOptionGroup 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    @Mapping(target = "options", ignore = true)
    // 옵션은 나중에 추가
    ProductOptionGroup toProductOptionGroupEntity(ProductDto.OptionGroup dto,
                                                  @Context Product product);

    // ProductOption 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "optionGroup", expression = "java(optionGroup)")
    ProductOption toProductOptionEntity(ProductDto.Option dto,
                                        @Context ProductOptionGroup optionGroup);

    // ProductImage 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    @Mapping(target = "option", expression = "java(option)")
    ProductImage toProductImageEntity(ProductDto.ImageDetail dto,
                                      @Context Product product,
                                      @Context ProductOption option);

    // -------------------------------------------------------------------------
    // Entity to DTO
    // -------------------------------------------------------------------------
    // Product 요약 변환
    @Mapping(source = "reviews", target = "rating")
    ProductDto.ProductDetail toProductDetailDto(Product product);

    // Product 상세 변환
    default ProductDto.ProductSummary toProductSummaryDto(Product product) {
        if (product == null) return null;

        return ProductDto.ProductSummary.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .basePrice(product.getPrice() != null ? product.getPrice().getBasePrice() : null)
                .salePrice(product.getPrice() != null ? product.getPrice().getSalePrice() : null)
                .currency(product.getPrice() != null ? product.getPrice().getCurrency() : null)
                .primaryImage(toProductImageSummary(product.getImages()))
                .brand(toBrandSummary(product.getBrand()))
                .seller(toSellerSummary(product.getSeller()))
                .rating(toRating(product.getReviews()))
                .reviewCount(product.getReviews().size())
                .inStock(product.getStatus().equals(ProductStatus.ACTIVE))
                .status(product.getStatus().name())
                .createdAt(product.getCreatedAt())
                .build();
    }

    // Primary Image 변환
    default ProductDto.ImageSummary toProductImageSummary(List<ProductImage> images) {
        // Null 체크
        if (images == null || images.isEmpty()) {
            return null;
        }

        ProductImage primaryImage = images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst().get();

        return ProductDto.ImageSummary.builder()
                .url(primaryImage.getUrl())
                .altText(primaryImage.getAltText())
                .build();
    }

    // BrandSummary 변환
    ProductDto.BrandSummary toBrandSummary(Brand brand);

    // SellerSummary 변환
    ProductDto.SellerSummary toSellerSummary(Seller seller);

    // CategorySummary 변환
    @Mapping(target = "parent", source = "parent")
    // 재귀처럼 매핑
    default ProductDto.CategorySummary toCategorySummary(ProductCategory productCategory){
        if (productCategory == null) return null;

        Category category = productCategory.getCategory();

        return ProductDto.CategorySummary.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parent(toParentCategory(category.getParent()))
                .build();
    }

    // ParentCategory 변환
    ProductDto.ParentCategory toParentCategory(Category category);

    default Double toRating(List<Review> reviews) {
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    // Option 변환
    ProductDto.Option toProductOptionDto(ProductOption productOption);

    default ProductDto.RatingSummary toRatingSummary(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return ProductDto.RatingSummary.builder()
                    .count(0)
                    .average(0.0)
                    .distribution(new HashMap<>())
                    .build();
        }

        // 평점 및 리뷰 개수 계산
        Double average = toRating(reviews);
        int size = reviews.size();

        // 평점 분포 계산 (1~5점)
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        // 각 리뷰 점수 추적
        for (Review review : reviews) {
            int rating = review.getRating();
            distribution.put(rating, distribution.getOrDefault(rating, 0) + 1);
        }

        return ProductDto.RatingSummary.builder()
                .count(size)
                .average(average)
                .distribution(distribution)
                .build();
    }

    // ProductTag 대응 변환
    default ProductDto.Tag toTag(ProductTag productTag) {
        if (productTag == null) return null;

        Tag tag = productTag.getTag();
        return ProductDto.Tag.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }

    // Product 기본 변환
    ProductDto.ProductBasic toProductBasicDto(Product product);

    // Map → JSON String 변환
    default String map(Map<String, Object> value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    default ProductDto.RelatedProduct toRelatedProduct(Product product) {
        if (product == null) return null;

        return ProductDto.RelatedProduct.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .primaryImage(toProductImageSummary(product.getImages()))
                .basePrice(product.getPrice().getBasePrice())
                .salePrice(product.getPrice().getSalePrice())
                .currency(product.getPrice().getCurrency())
                .build();

    }
    // String → Map 변환
    default Map<String, Object> map(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}
