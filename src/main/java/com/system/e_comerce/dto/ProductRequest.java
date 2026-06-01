package com.system.e_comerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "The product name is required")
        String name,

        @NotBlank(message = "The description is required")
        String description,

        @NotNull(message = "The price is required")
        @Min(value = 0, message = "The price cannot be negative")
        BigDecimal price,

        @NotNull(message = "The stock is required")
        @Min(value = 0, message = "The stock cannot be negative")
        Integer stock
) {
}
