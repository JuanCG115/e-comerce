package com.system.e_comerce.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Integer rating,
        String comment,
        String userEmail,
        String userFullName,
        LocalDateTime createdAt
) {
}
