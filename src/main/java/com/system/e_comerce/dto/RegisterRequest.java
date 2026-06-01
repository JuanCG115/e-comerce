package com.system.e_comerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "The name cannot be empty")
        @Size(max = 50, message = "The name cannot be longer than 50 characters")
        String firstName,

        @NotBlank(message = "The last name cannot be empty")
        @Size(max = 50, message = "The last name cannot be longer than 50 characters")
        String lastName,

        @NotBlank(message = "The email address cannot be empty")
        @Email(message = "The email format is invalid")
        @Size(max = 100, message = "The email address cannot be longer than 100 characters")
        String email,

        @NotBlank(message = "The password cannot be empty")
        @Size(min = 6, max = 120, message = "The password must be between 6 and 120 characters long.")
        String password
) { }
