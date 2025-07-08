package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.controller.dto.ReviewRequest;
import com.example.ecommerce.service.command.ReviewCommand;
import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.query.ReviewQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewRequestHandler {
    ReviewQuery.ListReviews toListReviewsQuery(Long productId, Integer rating, PaginationDto.PaginationRequest pagination);

    ReviewCommand.CreateReview toCreateReviewQuery(Long productId,
                                                   Long userId,
                                                   Integer rating,
                                                   String title,
                                                   String content);

    ReviewCommand.UpdateReview toUpdateReviewCommand(Long reviewId, Long userId, ReviewRequest.Review request);
}
