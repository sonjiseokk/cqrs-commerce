package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.controller.dto.ProductCreateRequest;
import com.example.ecommerce.controller.dto.ProductListRequest;
import com.example.ecommerce.controller.dto.ProductUpdateRequest;
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
    ProductQuery.ListProducts toListQuery(ProductListRequest request);

    @Named("toPagination")
    default PaginationDto.PaginationRequest toPaginationInfo(ProductListRequest request) {
        return PaginationDto.PaginationRequest.builder()
                .page(request.getPage())
                .size(request.getPerPage())
                .sort(request.getSort())
                .build();
    }

    ProductQuery.GetProduct toGetProduct(Long productId);

    ProductCommand.UpdateProduct toUpdateCommand(ProductUpdateRequest request);
}

