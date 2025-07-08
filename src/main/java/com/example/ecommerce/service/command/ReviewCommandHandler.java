package com.example.ecommerce.service.command;

import com.example.ecommerce.common.UnauthorizedReviewException;
import com.example.ecommerce.service.dto.ReviewDto;

public interface ReviewCommandHandler {
    ReviewDto.Review createReview(ReviewCommand.CreateReview command);

    ReviewDto.UpdateReview updateReview(ReviewCommand.UpdateReview command) throws UnauthorizedReviewException;

    void deleteReview(ReviewCommand.DeleteReview command);
}
