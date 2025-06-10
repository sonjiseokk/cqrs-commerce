package com.example.ecommerce.service.mapper;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.service.command.ProductCommand;
import com.example.ecommerce.service.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.io.IOException;
import java.util.Map;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductCommandMapper {
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

    // Product 요약 변환
    ProductDto.ProductDetail toProductDetailDto(Product product);

    // Product 상세 변환
    ProductDto.ProductSummary toProductSummaryDto(Product product);

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
