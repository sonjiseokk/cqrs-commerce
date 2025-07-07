package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.controller.dto.ReviewCreateRequest;
import com.example.ecommerce.controller.mapper.ReviewRequestHandler;
import com.example.ecommerce.service.command.ReviewCommand;
import com.example.ecommerce.service.command.ReviewCommandHandler;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ReviewDto;
import com.example.ecommerce.service.query.ReviewQuery;
import com.example.ecommerce.service.query.ReviewQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ReviewController {
    private final ReviewRequestHandler reviewRequestHandler;
    private final ReviewQueryHandler reviewQueryHandler;
    private final ReviewCommandHandler reviewCommandHandler;

    /**
     * 상품 리뷰 조회
     * GET /api/products/{id}/reviews
     *
     * 특정 상품의 리뷰 목록을 조회합니다.
     */
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<?>> getReviews(@PathVariable Long productId,
                                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                                     @RequestParam(required = false, defaultValue = "10") Integer perPage,
                                                     @RequestParam(required = false, defaultValue = "created_at:desc") String sort,
                                                     @RequestParam(required = false) Integer rating) {
        // 페이징 처리
        PaginationDto.PaginationRequest pageRequest = PaginationDto.PaginationRequest.builder()
                .page(page)
                .size(perPage)
                .sort(sort)
                .build();

        // Query 변환
        ReviewQuery.ListReviews query = reviewRequestHandler.toListReviewsQuery(productId, rating, pageRequest);

        // 비즈니스 로직 수행
        ReviewDto.ReviewPage response = reviewQueryHandler.getReviews(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "상품 리뷰를 성공적으로 조회했습니다."
        ));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<?>> createReview(@PathVariable Long productId,
                                                       @RequestBody ReviewCreateRequest request) {
        // :TODO 유저 ID 받는 부분 편집하기
        ReviewCommand.CreateReview query = reviewRequestHandler.toCreateReviewQuery(
                productId,
                1L,
                request.getRating(),
                request.getTitle(),
                request.getContent());

        ReviewDto.Review response = reviewCommandHandler.createReview(query);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "리뷰가 성공적으로 등록되었습니다."
        ));
    }
}
