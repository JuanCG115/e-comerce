package com.system.e_comerce.dto;

public record AuthenticationResponse(
        String token,
        String email,
        String role
) {
}
