package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.AuthRequest;
import com.example.taskManager.model.DTO.request.RegisterRequest;
import com.example.taskManager.service.AuthService;
import com.example.taskManager.service.JwtBlacklistService;
import com.example.taskManager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/register")
    public Response<?> register(@RequestBody RegisterRequest request) {
        return Response.success(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest request) {
       return ResponseEntity.of(Optional.ofNullable(authService.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtBlacklistService.blacklist(token);
            return ResponseEntity.ok(Response.success("Đăng xuất thành công"));
        }
        throw new RuntimeException("Token không hợp lệ hoặc không được cung cấp");
    }

}


