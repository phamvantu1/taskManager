package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.AuthRequest;
import com.example.taskManager.model.DTO.request.RegisterRequest;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;

    public Map<String, String> register(RegisterRequest registerRequest) {
        try {
            // Kiểm tra xem email đã tồn tại chưa
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new CustomException(ResponseCode.EMAIL_ALREADY_EXISTS);
            }

            // Tạo người dùng mới
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setDateOfBirth(registerRequest.getDateOfBirth());


            // Lưu người dùng vào cơ sở dữ liệu
            userRepository.save(user);

            // Trả về thông tin đăng ký thành công
            return Map.of("message", "User registered successfully");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public Map<String,String> login(AuthRequest authRequest){
        try {
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            // So sánh mật khẩu
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                throw new CustomException(ResponseCode.PASSWORD_INVALID);
            }

            // Tạo token và trả về
            return Map.of("access_token", jwtUtil.generateToken(user.getEmail()));
        }catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }

    }

    public Map<String, String> logout(HttpServletRequest request){
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtBlacklistService.blacklist(token);
                return Map.of("message", "Đăng xuất thành công");
            }
            throw new CustomException(ResponseCode.UNAUTHORIZED);
        }catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }

    }

}
