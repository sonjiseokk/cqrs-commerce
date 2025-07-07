package com.example.ecommerce.service.mapper;

import com.example.ecommerce.entity.Review;
import com.example.ecommerce.service.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ReviewMapper {
    default ReviewDto.ReviewSummary toReviewSummary(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return ReviewDto.ReviewSummary.builder()
                    .averageRating(0.0)
                    .totalCount(0)
                    .distribution(Map.of())
                    .build();
        }

        // 평균 평점 계산
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // totalCount 계산
        int totalCount = reviews.size();

        // 평점별 분포 계산
        Map<Integer, Integer> distribution = reviews.stream()
                .collect(Collectors.groupingBy(
                        Review::getRating,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        return ReviewDto.ReviewSummary.builder()
                .averageRating(averageRating)
                .totalCount(totalCount)
                .distribution(distribution)
                .build();

    }

    ReviewDto.Review toReviewDto(Review review);
}
