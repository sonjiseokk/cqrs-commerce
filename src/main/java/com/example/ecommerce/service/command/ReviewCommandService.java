package com.example.ecommerce.service.command;

import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.common.UnauthorizedReviewException;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.dto.ReviewDto;
import com.example.ecommerce.service.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommandService implements ReviewCommandHandler {
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewDto.Review createReview(ReviewCommand.CreateReview command) {
        // User 조회
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user", command.getUserId()));

        // Product 조회
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("product", command.getProductId()));

        // 엔티티 생성
        Review entity = reviewMapper.toEntity(command, product, user);
        reviewRepository.save(entity);

        return reviewMapper.toReviewDto(entity);
    }

    @Override
    @Transactional
    public ReviewDto.UpdateReview updateReview(ReviewCommand.UpdateReview command) throws UnauthorizedReviewException {
        // User 조회
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user", command.getUserId()));

        // Review 조회
        Review review = reviewRepository.findByIdWithUser(command.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("review", command.getReviewId()));

        // Exception : 작성자가 아닌 경우
        if (review.getUser().getId() != user.getId()) {
            throw new UnauthorizedReviewException("다른 사용자의 리뷰를 수정할 권한이 없습니다.");
        }

        review.update(
                command.getRating(),
                command.getTitle(),
                command.getContent()
        );

        // 명시적 세이브
        reviewRepository.save(review);

        return reviewMapper.toUpdateReview(review);
    }

    @Override
    @Transactional
    public void deleteReview(ReviewCommand.DeleteReview command) {
        // User 조회
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user", command.getUserId()));

        // Review 조회
        Review review = reviewRepository.findByIdWithUser(command.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("review", command.getReviewId()));

        // Exception : 작성자가 아닌 경우
        if (review.getUser().getId() != user.getId()) {
            throw new UnauthorizedReviewException("다른 사용자의 리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.deleteById(command.getReviewId());
    }
}
