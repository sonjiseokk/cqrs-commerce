package com.example.ecommerce.service.mapper;

import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.query.nosql.entity.BrandDocument;
import com.example.ecommerce.service.query.nosql.entity.ProductDocument;
import com.example.ecommerce.service.query.nosql.entity.SellerDocument;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ProductNoSqlMapper {
    // -------------------------------------------------------------------------
    // Entity to DTO
    // -------------------------------------------------------------------------
    public ProductDto.ProductDetail toProductDetailDto(ProductDocument productDocument,
                                                       SellerDocument sellerDocument,
                                                       BrandDocument brandDocument) {
        if (productDocument == null) return null;

        // Product 기본 정보 설정
        ProductDto.ProductDetail.ProductDetailBuilder builder = ProductDto.ProductDetail.builder()
                .id(productDocument.getId())
                .id(productDocument.getId())
                .name(productDocument.getName())
                .slug(productDocument.getSlug())
                .shortDescription(productDocument.getShortDescription())
                .fullDescription(productDocument.getFullDescription())
                .status(productDocument.getStatus())
                .createdAt(productDocument.getCreatedAt())
                .updatedAt(productDocument.getUpdatedAt());

        // Seller 정보 설정
        if (productDocument.getSeller() != null) {
            builder.seller(ProductDto.SellerDetail.builder()
                    .id(sellerDocument.getId())
                    .name(sellerDocument.getName())
                    .description(sellerDocument.getDescription())
                    .logoUrl(sellerDocument.getLogoUrl())
                    .rating(sellerDocument.getRating().doubleValue())
                    .contactEmail(sellerDocument.getContactEmail())
                    .contactPhone(sellerDocument.getContactPhone())
                    .build()
            );
        }

        // Brand 정보 설정
        if (brandDocument != null) {
            builder.brand(ProductDto.BrandDetail.builder()
                    .id(brandDocument.getId())
                    .name(brandDocument.getName())
                    .description(brandDocument.getDescription())
                    .logoUrl(brandDocument.getLogoUrl())
                    .website(brandDocument.getWebsite())
                    .build());
        }

        // Product Detail 설정
        if (productDocument.getDetail() != null) {
            builder.detail(ProductDto.Detail.builder()
                    .weight(productDocument.getDetail().getWeight())
                    .dimensions(productDocument.getDetail().getDimensions())
                    .materials(productDocument.getDetail().getMaterials())
                    .countryOfOrigin(productDocument.getDetail().getCountryOfOrigin())
                    .warrantyInfo(productDocument.getDetail().getWarrantyInfo())
                    .careInstructions(productDocument.getDetail().getCareInstructions())
                    .additionalInfo(productDocument.getDetail().getAdditionalInfo())
                    .build());
        }

        // Price 설정
        if (productDocument.getPrice() != null) {
            builder.price(ProductDto.Price.builder()
                    .basePrice(productDocument.getPrice().getBasePrice())
                    .salePrice(productDocument.getPrice().getSalePrice())
                    .costPrice(productDocument.getPrice().getCostPrice())
                    .currency(productDocument.getPrice().getCurrency())
                    .taxRate(productDocument.getPrice().getTaxRate())
                    .discountPercentage(productDocument.getPrice().getDiscountPercentage())
                    .build());
        }

        // 카테고리 정보 설정
        if (productDocument.getCategories() != null) {
            List<ProductDto.CategorySummary> categories = productDocument.getCategories().stream()
                    .map(this::toCategoryDto)
                    .collect(Collectors.toList());
            builder.categories(categories);
        }

        // 옵션 그룹 정보 설정
        if (productDocument.getOptionGroups() != null) {
            List<ProductDto.OptionGroup> optionGroups = productDocument.getOptionGroups().stream()
                    .map(this::toOptionGroupDto)
                    .collect(Collectors.toList());
            builder.optionGroups(optionGroups);
        }

        // 이미지 정보 설정
        if (productDocument.getImages() != null) {
            List<ProductDto.ImageDetail> images = productDocument.getImages().stream()
                    .map(this::toImageDto)
                    .collect(Collectors.toList());
            builder.images(images);
        }

        // 태그 정보 설정
        if (productDocument.getTags() != null) {
            List<ProductDto.Tag> tags = productDocument.getTags().stream()
                    .map(this::toTagDto)
                    .collect(Collectors.toList());
            builder.tags(tags);
        }

        // 평점 정보 설정
        if (productDocument.getRating() != null) {
            builder.rating(ProductDto.RatingSummary.builder()
                    .average(productDocument.getRating().getAverage())
                    .count(productDocument.getRating().getCount())
                    .distribution(productDocument.getRating().getDistribution())
                    .build());
        }

        return builder.build();
    }

    /**
     * 카테고리 정보 변환
     */
    private ProductDto.CategorySummary toCategoryDto(ProductDocument.CategoryInfo categoryInfo) {
        ProductDto.CategorySummary.CategorySummaryBuilder builder = ProductDto.CategorySummary.builder()
                .id(categoryInfo.getId())
                .name(categoryInfo.getName())
                .slug(categoryInfo.getSlug());

        if (categoryInfo.getParent() != null) {
            builder.parent(ProductDto.ParentCategory.builder()
                    .id(categoryInfo.getParent().getId())
                    .name(categoryInfo.getParent().getName())
                    .slug(categoryInfo.getParent().getSlug())
                    .build());
        }

        return builder.build();
    }

    /**
     * 옵션 그룹 정보 변환
     */
    private ProductDto.OptionGroup toOptionGroupDto(ProductDocument.OptionGroup optionGroup) {
        List<ProductDto.Option> options = new ArrayList<>();
        if (optionGroup.getOptions() != null) {
            options = optionGroup.getOptions().stream()
                    .map(this::toOptionDto)
                    .collect(Collectors.toList());
        }

        return ProductDto.OptionGroup.builder()
                .id(optionGroup.getId())
                .name(optionGroup.getName())
                .displayOrder(optionGroup.getDisplayOrder())
                .options(options)
                .build();
    }

    /**
     * 옵션 정보 변환
     */
    private ProductDto.Option toOptionDto(ProductDocument.Option option) {
        return ProductDto.Option.builder()
                .id(option.getId())
                .optionGroupId(option.getId()) // 옵션 그룹 ID는 별도로 설정해야 함
                .name(option.getName())
                .additionalPrice(option.getAdditionalPrice())
                .sku(option.getSku())
                .stock(option.getStock())
                .displayOrder(option.getDisplayOrder())
                .build();
    }

    /**
     * 이미지 정보 변환
     */
    private ProductDto.ImageDetail toImageDto(ProductDocument.Image image) {
        return ProductDto.ImageDetail.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .isPrimary(image.isPrimary())
                .displayOrder(image.getDisplayOrder())
                .optionId(image.getOptionId())
                .build();
    }

    /**
     * 태그 정보 변환
     */
    private ProductDto.Tag toTagDto(ProductDocument.TagInfo tagInfo) {
        return ProductDto.Tag.builder()
                .id(tagInfo.getId())
                .name(tagInfo.getName())
                .slug(tagInfo.getSlug())
                .build();
    }
}
