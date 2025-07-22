package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.AuthRequest;
import com.example.taskManager.model.DTO.request.ChangePasswordByOtpRequest;
import com.example.taskManager.model.DTO.request.RegisterRequest;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.model.entity.UserOTP;
import com.example.taskManager.repository.OtpRepository;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    public Map<String, String> register(RegisterRequest registerRequest) {
        try {
            // Kiểm tra xem email đã tồn tại chưa
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new CustomException(ResponseCode.EMAIL_ALREADY_EXISTS);
            }

            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                throw new CustomException(ResponseCode.CONFIRM_PASSWORD_NOT_MATCH);
            }

            // Tạo người dùng mới
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setActive(true);
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());

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
                    .orElseThrow(() -> new CustomException(ResponseCode.EMAIL_NOT_FOUND));

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

    public Map<String, String> forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            SecureRandom secureRandom = new SecureRandom();
            String OTP = String.format("%06d", secureRandom.nextInt(1000000));

            emailService.sendSimpleEmail(
                    email,
                    "Password Reset Request",
                    "OTP bạn nhận được có hạn trong 10 phút : "+ OTP
            );

            UserOTP userOTP = new UserOTP();
            userOTP.setUser(user);
            userOTP.setOtp_code(OTP);
            userOTP.setCreated_at(LocalDateTime.now());
            userOTP.setExpired_at(LocalDateTime.now().plusMinutes(10));
            userOTP.set_used(false);
            otpRepository.save(userOTP);

            return Map.of("message", "OTP sent to your email");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Forgot password failed: " + e.getMessage());
        }
    }

    public Map<String , String> verifyOtp(String email, String otp){
        try{
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            UserOTP userOTP = otpRepository.findByUserIdAndOtp(user.getId(), otp);

            if(userOTP == null) {
                throw new CustomException(ResponseCode.OTP_NOT_FOUND);
            }

            if (userOTP.is_used()) {
                throw new CustomException(ResponseCode.OTP_USED);
            }

            if (userOTP.getExpired_at().isBefore(LocalDateTime.now())) {
                throw new CustomException(ResponseCode.OTP_EXPIRED);
            }

            userOTP.set_used(true);
            otpRepository.save(userOTP);

           return Map.of("message", "OTP verified successfully");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Verify OTP failed: " + e.getMessage());
        }
    }

    public Map<String, String> changePasswordByOtp(ChangePasswordByOtpRequest changePasswordByOtpRequest) {
        try {
            if (!changePasswordByOtpRequest.getNewPassword().equals(changePasswordByOtpRequest.getConfirmNewPassword())) {
                throw new CustomException(ResponseCode.CONFIRM_PASSWORD_NOT_MATCH);
            }
            User user = userRepository.findByEmail(changePasswordByOtpRequest.getEmail())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            user.setPassword(passwordEncoder.encode(changePasswordByOtpRequest.getNewPassword()));
            userRepository.save(user);

            return Map.of("message", "Password changed successfully");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Change password failed: " + e.getMessage());
        }
    }

}
