package com.system.e_comerce.service;

import com.system.e_comerce.dto.ReviewRequest;
import com.system.e_comerce.dto.ReviewResponse;
import com.system.e_comerce.model.User;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long productId, ReviewRequest request, User authenticatedUser);
    List<ReviewResponse> getReviewsByProductId(Long productId);
}
