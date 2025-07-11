package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.ProductResponse;
import com.example.ecommerce.service.query.rdb.MainQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class MainController {
    private final MainQueryService mainQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> main() {
        ProductResponse.MainPage response = mainQueryService.main();

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "메인 페이지 상품 목록을 성공적으로 조회했습니다."
        ));
    }
}
