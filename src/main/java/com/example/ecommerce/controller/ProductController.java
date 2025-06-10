package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.ProductCreateRequest;
import com.example.ecommerce.controller.mapper.ProductRequestMapper;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.command.ProductCommand;
import com.example.ecommerce.service.command.ProductCommandHandler;
import com.example.ecommerce.service.dto.ProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductCommandHandler commandHandler;
    private final ProductRequestMapper mapper;

    @PostMapping()
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        ProductCommand.CreateProduct command = mapper.toCreateCommand(request);

        ProductDto.ProductBasic response = commandHandler.createProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "상품 등록이 완료되었습니다."));
    }
}
