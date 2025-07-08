package com.example.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewRequest {
    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Review {
        private Integer rating;
        private String title;
        private String content;
    }
}
