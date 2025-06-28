package com.example.ecommerce.controller.dto;

import com.example.ecommerce.service.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGetResponse {
    private ProductDto.ProductDetail data;
}
