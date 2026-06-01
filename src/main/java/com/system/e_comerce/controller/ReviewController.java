package com.system.e_comerce.controller;

import com.system.e_comerce.dto.ReviewRequest;
import com.system.e_comerce.dto.ReviewResponse;
import com.system.e_comerce.model.User;
import com.system.e_comerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // let comment
    // only users authenticated
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User authenticatedUser
            ) {
        return new ResponseEntity<>(reviewService.createReview(productId, request, authenticatedUser), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }
}
