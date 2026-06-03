package com.system.e_comerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.e_comerce.dto.ReviewRequest;
import com.system.e_comerce.dto.ReviewResponse;
import com.system.e_comerce.model.Role;
import com.system.e_comerce.model.User;
import com.system.e_comerce.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ReviewControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService)).build();
    }

    @Test
    @DisplayName("Should return list of reviews with HTTP 200 OK when product exists")
    void getReviewsByProduct_ShouldReturnListAnd200OK() throws Exception {
        // 1. arrange
        Long productId = 1L;
        ReviewResponse r1 = new ReviewResponse(10L, 5, "Excellent", "raul@ecommerce.com", "Raul Martinez", LocalDateTime.now());
        ReviewResponse r2 = new ReviewResponse(11L, 4, "Good", "admin@ecommerce.com", "Juan Camarillo", LocalDateTime.now());

        when(reviewService.getReviewsByProductId(productId)).thenReturn(List.of(r1, r2));

        // 2. act and assert
        mockMvc.perform(get("/api/products/{productId}/reviews", productId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("anonymous")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].comment").value("Excellent"))
                .andExpect(jsonPath("$[1].userFullName").value("Juan Camarillo"));

        // 3. verification
        verify(reviewService, times(1)).getReviewsByProductId(productId);
    }

    @Test
    @DisplayName("Should create a review and return HTTP 201 Created when user is authenticated")
    void createReview_ShouldReturnCreatedAndReviewResponse() throws Exception {
        // 1. arrange
        Long productId = 1L;

        ReviewRequest request = new ReviewRequest(5, "Excellent product");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("admin@ecommerce.com");

        mockUser.setRole(Role.ADMIN);

        ReviewResponse expectedResponse = new ReviewResponse(
                20L,
                5,
                "Excellent product",
                "admin@ecommerce.com",
                "Juan Camarillo",
                LocalDateTime.now()
        );

        when(reviewService.createReview(eq(productId), any(ReviewRequest.class), any(User.class)))
                .thenReturn(expectedResponse);

        // 2. act and assert
        mockMvc.perform(post("/api/products/{productId}/reviews", productId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request))
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent product"))
                .andExpect(jsonPath("$.userFullName").value("Juan Camarillo"));

        // 3. verification
        verify(reviewService, times(1)).createReview(eq(productId), any(), any());
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when review request validation fails")
    void createReview_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // 1. arrange
        Long productId = 1L;
        ReviewRequest invalidRequest = new ReviewRequest(5, "");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("raul@ecommerce.com");
        mockUser.setRole(Role.CUSTOMER);

        // 2. act and assert
        mockMvc.perform(post("/api/products/{productId}/reviews", productId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(status().isBadRequest());

        // 3. verification
        verify(reviewService, never()).createReview(any(), any(), any());
    }
}
