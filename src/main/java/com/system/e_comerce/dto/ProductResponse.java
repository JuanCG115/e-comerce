package com.system.e_comerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Double averageRating,
        LocalDateTime createdAt
) {
}
