package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.controller.dto.ProductCreateRequest;
import com.example.ecommerce.service.command.ProductCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {
    ProductCommand.CreateProduct toCreateCommand(ProductCreateRequest request);
}

