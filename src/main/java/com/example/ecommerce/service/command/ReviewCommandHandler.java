package com.example.ecommerce.service.command;

import com.example.ecommerce.service.dto.ReviewDto;

public interface ReviewCommandHandler {
    ReviewDto.Review createReview(ReviewCommand.CreateReview command);
}
