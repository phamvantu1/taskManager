package com.example.taskManager.service;


import com.example.taskManager.model.entity.BlacklistedToken;
import com.example.taskManager.repository.BlacklistedTokenRepository;
import com.example.taskManager.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final BlacklistedTokenRepository repository;
    private final JwtUtil jwtUtil;

    @Scheduled(cron = "0 0 0 * * *")  // Runs daily at midnight
    public void cleanExpiredTokens() {
        Date now = new Date();
        repository.deleteAllByExpirationDateBefore(now);
    }

    public void blacklist(String token) {
        Date expiration = jwtUtil.extractExpiration(token);
        if (!repository.existsByToken(token)) {
            repository.save(new BlacklistedToken(null, token, expiration));
        }
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}