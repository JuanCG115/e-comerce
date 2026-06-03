package com.system.e_comerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.e_comerce.dto.AuthenticationResponse;
import com.system.e_comerce.dto.LoginRequest;
import com.system.e_comerce.dto.RegisterRequest;
import com.system.e_comerce.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    @DisplayName("Should register a new user and return HTTP 201 Created")
    void register_ShouldReturnCreatedAndToken() throws Exception {
        // 1. arrange
        RegisterRequest request = new RegisterRequest("Juan", "Camarillo", "admin@ecommerce.com", "password123");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("mocked-jwt-token-for-register", "admin@ecommerce.com", "ADMIN");

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // 2. act and assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token-for-register"));

        // 3. verification
        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should login successfully and return HTTP 200 OK")
    void login_ShouldReturnOkAndToken() throws Exception {
        // 1. arrange
        LoginRequest request = new LoginRequest("admin@ecommerce.com", "password123");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("mocked-jwt-token-for-login", "admin@ecommerce.com", "ADMIN");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // 2. act and assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token-for-login"));

        // 3. verification
        verify(authService, times(1)).login(any(LoginRequest.class));
    }
}