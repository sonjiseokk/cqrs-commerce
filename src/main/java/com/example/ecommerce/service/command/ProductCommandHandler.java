package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ProductCommandHandler {
    ProductDto.ProductBasic createProduct(ProductCommand.CreateProduct command);

    ProductDto.ProductBasic updateProduct(Long productId, ProductCommand.UpdateProduct command) throws JsonProcessingException;

    void deleteProduct(ProductCommand.DeleteProduct command);
}
