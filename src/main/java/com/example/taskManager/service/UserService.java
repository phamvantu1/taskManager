package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.AuthRequest;
import com.example.taskManager.model.DTO.request.RegisterRequest;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
    }
}
