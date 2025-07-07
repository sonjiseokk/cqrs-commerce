package com.example.ecommerce.controller.mapper;

import com.example.ecommerce.service.dto.PaginationDto;
import com.example.ecommerce.service.query.ReviewQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewRequestHandler {
    ReviewQuery.ListReviews toListReviewsQuery(Long productId, Integer rating, PaginationDto.PaginationRequest pagination);

}
