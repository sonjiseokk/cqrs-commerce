package com.example.ecommerce.service.query;

import com.example.ecommerce.service.dto.PaginationDto;
import lombok.Builder;
import lombok.Data;

public class ReviewQuery {
    @Data
    @Builder
    public static class ListReviews {
        private Long productId;
        private Integer rating;
        private PaginationDto.PaginationRequest pagination;
    }



}
