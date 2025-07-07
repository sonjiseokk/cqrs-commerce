package com.example.ecommerce.service.command;

import com.example.ecommerce.common.ResourceNotFoundException;
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
}
