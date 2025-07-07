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
     * <p>
     * 새로운 상품을 등록합니다.
     *
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody @Valid ProductRequest.Product request) {
        ProductCommand.CreateProduct command = productRequestMapper.toCreateCommand(request);

        ProductDto.ProductBasic response = commandHandler.createProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "상품 등록이 완료되었습니다."));
    }

    /**
     * 상품 목록 조회
     * GET /api/products
     * <p>
     * 상품 목록을 조회합니다.
     *
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getProducts(@ModelAttribute ProductRequest.ListRequest request) {
        ProductQuery.ListProducts query = productRequestMapper.toListQueryCommand(request);

        ProductResponse.GetProductList response = queryHandler.getProducts(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 목록을 성공적으로 조회했습니다."));
    }

    /**
     * 상품 상세 조회
     * GET /api/products/{id}
     * <p>
     * 특정 상품의 상세 정보를 조회합니다.
     *
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> getProduct(@PathVariable Long productId) {
        ProductQuery.GetProduct query = productRequestMapper.toGetProductCommand(productId);

        ProductResponse.GetProduct response = queryHandler.getProduct(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 상세 정보를 성공적으로 조회했습니다."
        ));
    }

    /**
     * 상품 수정
     * PUT /api/products/{id}
     * <p>
     * 특정 상품 정보를 수정합니다.
     *
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable Long productId,
                                                        @RequestBody ProductRequest.Product request) throws Exception {
        ProductCommand.UpdateProduct command = productRequestMapper.toUpdateCommand(request, productId);

        ProductDto.ProductBasic response = commandHandler.updateProduct(command);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품이 성공적으로 수정되었습니다."
        ));
    }

    /**
     * 상품 삭제
     * DELETE /api/products/{id}
     *
     * 특정 상품을 삭제합니다.
     *
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long productId) {
        ProductCommand.DeleteProduct command = productRequestMapper.toDeleteProductCommand(productId);

        commandHandler.deleteProduct(command);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "상품이 성공적으로 삭제되었습니다."
        ));
    }

    /**
     * 상품 옵션 수정
     * PUT /api/products/{id}/options/{optionId}
     *
     * 특정 상품의 옵션을 수정합니다.
     */
    @PutMapping("/{productId}/options/{optionId}")
    public ResponseEntity<ApiResponse<?>> updateOption(@PathVariable Long productId,
                                                       @PathVariable Long optionId,
                                                       @RequestBody ProductRequest.OptionDto request) throws Exception {
        ProductCommand.UpdateOption command = productRequestMapper.toUpdateOptionCommand(request, productId, optionId);

        ProductDto.Option response = commandHandler.updateOption(command);
        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 옵션이 성공적으로 수정되었습니다."
        ));
    }

    /**
     * 상품 옵션 삭제
     * DELETE /api/products/{id}/options/{optionId}
     *
     * 특정 상품의 옵션을 삭제합니다.
     */
    @DeleteMapping("/{productId}/options/{optionId}")
    public ResponseEntity<ApiResponse<?>> deleteOption(@PathVariable Long productId,
                                                       @PathVariable Long optionId) {
        ProductCommand.DeleteOption command = productRequestMapper.toDeleteOptionCommand(productId, optionId);

        commandHandler.deleteOption(command);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "상품 옵션이 성공적으로 삭제되었습니다."
        ));
    }

    /**
     * 상품 이미지 추가
     * POST /api/products/{id}/images
     * <p>
     * 특정 상품에 이미지를 추가합니다.
     */
    @PostMapping("/{productId}/images")
    public ResponseEntity<ApiResponse<?>> createImage(@PathVariable Long productId,
                                                      @RequestBody ProductRequest.ImageDto request) {
        ProductCommand.CreateImage command = productRequestMapper.toCreateImageCommand(request, productId);

        ProductDto.ImageDetail response = commandHandler.createImage(command);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 이미지가 성공적으로 추가되었습니다."
        ));
    }

    /**
     * 상품 이미지 추가
     * DELETE /api/products/{id}/images/{imageId}
     * <p>
     * 특정 상품의 특정 이미지를 삭제합니다.
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<?>> deleteImage(@PathVariable Long productId,
                                                      @PathVariable Long imageId) {
        ProductCommand.DeleteImage command = productRequestMapper.toDeleteImageCommand(productId, imageId);

        commandHandler.deleteImage(command);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "상품 이미지가 성공적으로 삭제되었습니다."
        ));
    }
}
