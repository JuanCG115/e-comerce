package com.system.e_comerce.service.impl;

import com.system.e_comerce.dto.ReviewRequest;
import com.system.e_comerce.dto.ReviewResponse;
import com.system.e_comerce.model.Product;
import com.system.e_comerce.model.Review;
import com.system.e_comerce.model.User;
import com.system.e_comerce.repository.ProductRepository;
import com.system.e_comerce.repository.ReviewRepository;
import com.system.e_comerce.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long productId, ReviewRequest request, User authenticatedUser) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (reviewRepository.existsByProductIdAndUserId(productId, authenticatedUser.getId())) {
            throw new IllegalArgumentException("You have already submitted a review for this product.");
        }

        Review review = Review.builder()
                .rating(request.rating())
                .comment(request.comment())
                .product(product)
                .user(authenticatedUser)
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        String fullName = review.getUser().getName() + " " + review.getUser().getLastName();
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getEmail(),
                fullName,
                review.getCreatedAt()
        );
    }
}
