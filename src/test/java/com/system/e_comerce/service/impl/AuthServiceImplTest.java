package com.system.e_comerce.service.impl;

import com.system.e_comerce.dto.AuthenticationResponse;
import com.system.e_comerce.dto.LoginRequest;
import com.system.e_comerce.dto.RegisterRequest;
import com.system.e_comerce.model.Role;
import com.system.e_comerce.model.User;
import com.system.e_comerce.repository.UserRepository;
import com.system.e_comerce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    // mocks
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    @DisplayName("Should register a user successfully and adapt to any role assigned by the service")
    void register_ShouldSaveUserAndReturnToken_WhenRequestIsValid() {
        // 1. arrange
        RegisterRequest request = new RegisterRequest("Juan", "Camarillo", "admin@ecommerce.com", "password123");
        when(userRepository.existsByEmail("admin@ecommerce.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(jwtService.generateToken(any(), any(User.class))).thenReturn("mocked_jwt_token");

        // 2. act
        AuthenticationResponse response = authService.register(request);

        // 3. assert
        assertNotNull(response, "The response should not be null");
        assertEquals("mocked_jwt_token", response.token());
        assertEquals("admin@ecommerce.com", response.email());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // verification
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(savedUser.getRole().name(), response.role());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to register an existing email")
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // 1. arrange
        RegisterRequest request = new RegisterRequest("Raul", "Martinez", "raul@ecommerce.com", "raul123");
        when(userRepository.existsByEmail("raul@ecommerce.com")).thenReturn(true);

        // 2. act and assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });

        // 3. verification
        assertEquals("The email address is already registered", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully and return token adapting to the user's role")
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // 1. arrange
        LoginRequest request = new LoginRequest("admin@ecommerce.com", "password123");
        // create test user
        User existingUser = User.builder()
                .name("Juan")
                .lastName("Camarillo")
                .email("admin@ecommerce.com")
                .password("encoded_password")
                .role(Role.ADMIN)
                .build();
        when(userRepository.findByEmail("admin@ecommerce.com")).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(any(), any(User.class))).thenReturn("mocked_jwt_token");

        // 2. act
        AuthenticationResponse response = authService.login(request);

        // 3. assert
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.token());
        assertEquals("admin@ecommerce.com", response.email());

        assertEquals(existingUser.getRole().name(), response.role());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user email does not exist during login")
    void login_ShouldThrowException_WhenUserNotFound() {
        // 1. arrange
        LoginRequest request = new LoginRequest("inexistent@ecommerce.com", "password123");

        when(userRepository.findByEmail("inexistent@ecommerce.com")).thenReturn(Optional.empty());

        // 2. act and assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(request);
        });

        // 3. verification
        assertEquals("User not found", exception.getMessage());
        verify(jwtService, never()).generateToken(any(), any(User.class));
    }
}
