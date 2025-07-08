package com.example.ecommerce.service.query;

import com.example.ecommerce.service.dto.ReviewDto;

public interface ReviewQueryHandler {
    ReviewDto.ReviewPage getReviews(ReviewQuery.ListReviews query);
}
