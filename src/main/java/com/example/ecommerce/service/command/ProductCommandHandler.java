package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ProductCommandHandler {
    ProductDto.ProductBasic createProduct(ProductCommand.CreateProduct command);

    ProductDto.ProductBasic updateProduct(ProductCommand.UpdateProduct command) throws JsonProcessingException;

    void deleteProduct(ProductCommand.DeleteProduct command);
    ProductDto.Option updateOption(ProductCommand.UpdateOption command);

    void deleteOption(ProductCommand.DeleteOption command);

    ProductDto.ImageDetail createImage(ProductCommand.CreateImage command);

    void deleteImage(ProductCommand.DeleteImage command);
}
