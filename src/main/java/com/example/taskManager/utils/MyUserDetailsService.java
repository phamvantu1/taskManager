package com.example.taskManager.utils;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tùy vào hệ thống, có thể lấy user từ database

            return User.withUsername(username)
                    .password(new BCryptPasswordEncoder().encode("password")) // Mật khẩu mã hóa
                    .roles("USER") // Vai trò của người dùng
                    .build();

    }
}
