package com.system.e_comerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "The email address is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "The password is required")
        String password
) { }
