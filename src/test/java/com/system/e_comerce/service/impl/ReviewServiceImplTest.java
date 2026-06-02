package com.system.e_comerce.service.impl;


import com.system.e_comerce.dto.ReviewRequest;
import com.system.e_comerce.dto.ReviewResponse;
import com.system.e_comerce.model.Product;
import com.system.e_comerce.model.Review;
import com.system.e_comerce.model.User;
import com.system.e_comerce.repository.ProductRepository;
import com.system.e_comerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, productRepository);
    }

    @Test
    @DisplayName("Should create a review successfully when product exists and user hasn't reviewed it yet")
    void createReview_ShouldSaveReviewAndReturnResponse_WhenRequestIsValid() {
        // 1. arrange
        Long productId = 1L;
        ReviewRequest request = new ReviewRequest(5, "Great product");

        User authenticatedUser = User.builder()
                .id(10L)
                .name("Raul")
                .lastName("Martinez")
                .email("raul@ecommerce.com")
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Laptop Gamer")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        when(reviewRepository.existsByProductIdAndUserId(productId, authenticatedUser.getId())).thenReturn(false);

        Review reviewSimulated = Review.builder()
                .id(100L)
                .rating(request.rating())
                .comment(request.comment())
                .product(product)
                .user(authenticatedUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(reviewSimulated);

        // 2. act
        ReviewResponse response = reviewService.createReview(productId, request, authenticatedUser);

        // 3. assert
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(5, response.rating());
        assertEquals("Great product", response.comment());
        assertEquals("raul@ecommerce.com", response.userEmail());

        assertEquals("Raul Martinez", response.userFullName());

        // verification
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(1)).save(reviewCaptor.capture());

        Review reviewSaved = reviewCaptor.getValue();
        assertEquals(authenticatedUser, reviewSaved.getUser());
        assertEquals(product, reviewSaved.getProduct());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product does not exist")
    void createReview_ShouldThrowException_WhenProductNotFound() {
        // 1. arrange
        Long productId = 999L;
        ReviewRequest request = new ReviewRequest(5, "Excellent product");
        User authenticatedUser = User.builder().id(10L).build();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // 2. act and assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(productId, request, authenticatedUser);
        });

        // 3. verifications
        assertEquals("Product not found", exception.getMessage());

        verify(reviewRepository, never()).existsByProductIdAndUserId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user has already submitted a review for the product")
    void createReview_ShouldThrowException_WhenUserAlreadyReviewed() {
        // 1. arrange
        Long productId = 1L;
        ReviewRequest request = new ReviewRequest(4, "Other comment");

        User authenticatedUser = User.builder().id(10L).build();
        Product product = Product.builder()
                .id(productId)
                .name("Mouse Gamer")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        when(reviewRepository.existsByProductIdAndUserId(productId, authenticatedUser.getId())).thenReturn(true);

        // 2. act and assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(productId, request, authenticatedUser);
        });

        // 3. verifications
        assertEquals("You have already submitted a review for this product.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should return a list of review responses when product exists")
    void getReviewsByProductId_ShouldReturnList_WhenProductExists() {
        // 1. arrange
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        User user = User.builder().name("Juan").lastName("Camarillo").email("admin@ecommerce.com").build();
        Review review1 = Review.builder().id(101L).rating(5).comment("Great").user(user).build();
        Review review2 = Review.builder().id(102L).rating(4).comment("Excellent").user(user).build();

        List<Review> mockReviews = List.of(review1, review2);

        when(reviewRepository.findByProductId(productId)).thenReturn(mockReviews);

        // 2. act
        List<ReviewResponse> result = reviewService.getReviewsByProductId(productId);

        // 3. assert
        assertNotNull(result, "The resulting list cannot be null");
        assertEquals(2, result.size(), "The list must contain exactly 2 reviews.");

        assertEquals(101L, result.get(0).id());
        assertEquals("Great", result.get(0).comment());
        assertEquals("Juan Camarillo", result.get(0).userFullName());

        // verification
        verify(reviewRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to get reviews for a non-existing product")
    void getReviewsByProductId_ShouldThrowException_WhenProductNotFound() {
        // 1. arrange
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // 2. act and assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewsByProductId(productId);
        });

        // 3. verifications
        assertEquals("Product not found", exception.getMessage());
        verify(reviewRepository, never()).findByProductId(anyLong());
    }
}
