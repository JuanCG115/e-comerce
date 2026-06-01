package com.system.e_comerce.service.impl;

import com.system.e_comerce.dto.AuthenticationResponse;
import com.system.e_comerce.dto.LoginRequest;
import com.system.e_comerce.dto.RegisterRequest;
import com.system.e_comerce.model.User;
import com.system.e_comerce.repository.UserRepository;
import com.system.e_comerce.security.AuthService;
import com.system.e_comerce.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("The email address is already registered");
        }

        User user = User.builder()
                .name(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(com.system.e_comerce.model.Role.CUSTOMER)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);

        return new AuthenticationResponse(jwtToken, user.getEmail(), user.getRole().name());
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), user);

        return new AuthenticationResponse(jwtToken, user.getEmail(), user.getRole().name());
    }
}
