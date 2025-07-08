package com.example.ecommerce.service.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewCommand {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateReview {
        private Long productId;
        private Long userId;
        private Integer rating;
        private String title;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateReview {
        private Long reviewId;
        private Long userId;
        private Integer rating;
        private String title;
        private String content;
    }
}
