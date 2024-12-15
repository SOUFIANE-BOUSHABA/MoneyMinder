package com.example.moneyminder.service;

import com.example.moneyminder.entity.RefreshToken;
import com.example.moneyminder.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> findByToken(String token);
}
