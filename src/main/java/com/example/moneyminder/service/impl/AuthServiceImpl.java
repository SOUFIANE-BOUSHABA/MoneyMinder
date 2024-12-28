package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.LoginRequest;
import com.example.moneyminder.DTOs.RegisterRequest;
import com.example.moneyminder.DTOs.TokenResponse;
import com.example.moneyminder.entity.Role;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.exception.CustomException;
import com.example.moneyminder.repository.RoleRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.AuthService;
import com.example.moneyminder.service.RefreshTokenService;
import com.example.moneyminder.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER");
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(userRole)
                .isActive(true)
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials");
        }

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        String role = user.getRole().getName();
        String firstname = user.getFirstName();
        String lastname = user.getLastName();

        return new TokenResponse(accessToken, refreshToken, role, firstname, lastname);

    }

    @Override
    public TokenResponse refreshAccessToken(String refreshToken) {
        var token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException("Invalid refresh token"));

        String newAccessToken = jwtUtils.generateAccessToken(token.getUser().getEmail());
        String role = token.getUser().getRole().getName();
        String firstname = token.getUser().getFirstName();
        String lastname = token.getUser().getLastName();


        return new TokenResponse(newAccessToken, refreshToken , role , firstname , lastname);
    }
}
