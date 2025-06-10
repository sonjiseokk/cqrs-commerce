package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ProductDto;

public interface ProductCommandHandler {
    ProductDto.ProductBasic createProduct(ProductCommand.CreateProduct command);
}
