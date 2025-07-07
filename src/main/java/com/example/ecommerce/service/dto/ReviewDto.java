package com.example.ecommerce.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReviewDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Review {
        private Long id;
        private User user;
        private Double rating;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean verifiedPurchase;
        private Long helpfulVotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewSummary {
        private Double averageRating;
        private Integer totalCount;
        private Map<Integer, Integer> distribution; // 평점별 개수
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Long id;
        private String name;
        private String avatarUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewPage {
        private List<Review> items;
        private ReviewSummary summary;
        private PaginationDto.PaginationInfo pagination;
    }
}
