package com.system.e_comerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull(message = "The rating is required")
        @Min(value = 1, message = "The minimum rating is 1")
        @Max(value = 5, message = "The maximum rating is 5")
        Integer rating,

        @NotBlank(message = "The comment cannot be empty")
        String comment
) {
}
