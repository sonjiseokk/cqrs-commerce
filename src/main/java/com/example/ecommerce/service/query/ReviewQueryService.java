package com.example.ecommerce.service.query;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.dto.ReviewDto;
import com.example.ecommerce.service.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewQueryService implements ReviewQueryHandler {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDto.ReviewPage getReviews(ReviewQuery.ListReviews query) {
        // 1. product 조회
        // - 없으면 예외처리
        Product product = productRepository.findById(query.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", query.getProductId()));

        // 2. 리뷰 요약
        // 제품의 모든 리뷰 조회 (Summary 용)
        List<Review> reviews = reviewRepository.findAllByProduct(product);

        // toSummary
        ReviewDto.ReviewSummary summary = reviewMapper.toReviewSummary(reviews);

        // 3. 리뷰 목록 조회
        // 페이징 정보 추출
        Pageable pageable = query.getPagination().toPageable();

        // 분기 지점
        // Rating 필터가 있는 경우
        Page<Review> reviewItems;
        if (query.getRating() != null) {
            reviewItems = reviewRepository.findByProductRating(product, query.getRating(), pageable);
        } else {
            // Rating 필터가 없는 경우
            reviewItems = reviewRepository.findByProduct(product, pageable);
        }

        // DTO로 변환
        List<ReviewDto.Review> items = reviewItems.stream()
                .map(reviewMapper::toReviewDto)
                .toList();

        // 4. Pagination 변환
        PaginationDto.PaginationInfo paginationInfo = PaginationDto.PaginationInfo.builder()
                .totalItems((int) reviewItems.getTotalElements())
                .totalPages(reviewItems.getTotalPages())
                .currentPage(reviewItems.getNumber() + 1) // 0-based to 1-based
                .perPage(reviewItems.getSize())
                .build();

        return ReviewDto.ReviewPage.builder()
                .items(items)
                .summary(summary)
                .pagination(paginationInfo)
                .build();
    }
}
