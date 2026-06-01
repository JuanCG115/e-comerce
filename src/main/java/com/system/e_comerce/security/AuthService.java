package com.system.e_comerce.security;

import com.system.e_comerce.dto.AuthenticationResponse;
import com.system.e_comerce.dto.LoginRequest;
import com.system.e_comerce.dto.RegisterRequest;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
}
