package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.controller.dto.*;
import com.example.ecommerce.service.command.ProductCommand;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.query.ProductQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {
    ProductCommand.CreateProduct toCreateCommand(ProductCreateRequest request);

    @Mapping(target = "pagination", source = "request", qualifiedByName = "toPagination")
    ProductQuery.ListProducts toListQueryCommand(ProductListRequest request);

    @Named("toPagination")
    default PaginationDto.PaginationRequest toPaginationInfo(ProductListRequest request) {
        return PaginationDto.PaginationRequest.builder()
                .page(request.getPage())
                .size(request.getPerPage())
                .sort(request.getSort())
                .build();
    }

    ProductQuery.GetProduct toGetProductCommand(Long productId);

    ProductCommand.UpdateProduct toUpdateCommand(ProductUpdateRequest request, Long productId);

    ProductCommand.DeleteProduct toDeleteProductCommand(Long productId);

    ProductCommand.UpdateOption toUpdateOptionCommand(ProductOptionUpdateRequest request, Long productId, Long optionId);

    ProductCommand.DeleteOption toDeleteOptionCommand(Long productId, Long optionId);

    ProductCommand.CreateImage toCreateImageCommand(ProductImageCreateRequest request, Long productId);
}

