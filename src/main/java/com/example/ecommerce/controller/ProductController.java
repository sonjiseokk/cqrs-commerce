package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.*;
import com.example.ecommerce.controller.mapper.ProductRequestMapper;
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

    /**
     * 상품 등록
     * POST /api/products
     *
     * 새로운 상품을 등록합니다.
     * @param request
     * @return
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        ProductCommand.CreateProduct command = productRequestMapper.toCreateCommand(request);

        ProductDto.ProductBasic response = commandHandler.createProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "상품 등록이 완료되었습니다."));
    }

    /**
     * 상품 목록 조회
     * GET /api/products
     *
     * 상품 목록을 조회합니다.
     * @param request
     * @return
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getProducts(@ModelAttribute ProductListRequest request) {
        ProductQuery.ListProducts query = productRequestMapper.toListQuery(request);

        ProductListResponse response = queryHandler.getProducts(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 목록을 성공적으로 조회했습니다."));
    }

    /**
     * 상품 상세 조회
     * GET /api/products/{id}
     *
     * 특정 상품의 상세 정보를 조회합니다.
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> getProduct(@PathVariable Long productId) {
        ProductQuery.GetProduct query = productRequestMapper.toGetProduct(productId);

        ProductGetResponse response = queryHandler.getProduct(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 상세 정보를 성공적으로 조회했습니다."
        ));
    }

    /**
     * 상품 수정
     * PUT /api/products/{id}
     *
     * 특정 상품 정보를 수정합니다.
     * @param productId
     * @return
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable Long productId, @ModelAttribute ProductUpdateRequest request) {
        ProductCommand.UpdateProduct command = productRequestMapper.toUpdateProduct(request);

        ProductDto.ProductBasic response = commandHandler.updateProduct(productId, command);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품이 성공적으로 수정되었습니다."
        ));
    }
}
