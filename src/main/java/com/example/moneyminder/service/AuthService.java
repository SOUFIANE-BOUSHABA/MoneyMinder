package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.LoginRequest;
import com.example.moneyminder.DTOs.RegisterRequest;
import com.example.moneyminder.DTOs.TokenResponse;

public interface AuthService {
    String register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refreshAccessToken(String refreshToken);
}
