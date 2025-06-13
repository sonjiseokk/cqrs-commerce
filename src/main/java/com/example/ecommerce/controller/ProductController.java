package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.ProductCreateRequest;
import com.example.ecommerce.controller.dto.ProductListRequest;
import com.example.ecommerce.controller.dto.ProductListResponse;
import com.example.ecommerce.controller.mapper.ProductRequestMapper;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.command.ProductCommand;
import com.example.ecommerce.service.command.ProductCommandHandler;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.query.ProductQuery;
import com.example.ecommerce.service.query.ProductQueryHandler;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductQueryHandler queryHandler;
    private final ProductCommandHandler commandHandler;
    private final ProductRequestMapper productRequestMapper;

    @PostMapping()
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        ProductCommand.CreateProduct command = productRequestMapper.toCreateCommand(request);

        ProductDto.ProductBasic response = commandHandler.createProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "상품 등록이 완료되었습니다."));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getProducts(@ModelAttribute ProductListRequest request) {
        ProductQuery.ListProducts query = productRequestMapper.toListQuery(request);

        ProductListResponse response = queryHandler.getProducts(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 목록을 성공적으로 조회했습니다."));
    }
}
