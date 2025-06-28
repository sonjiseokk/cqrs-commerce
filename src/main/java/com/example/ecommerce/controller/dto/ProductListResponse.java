package com.example.ecommerce.controller.dto;

import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private List<ProductDto.ProductSummary> items;
    private PaginationDto.PaginationInfo pagination;
}
