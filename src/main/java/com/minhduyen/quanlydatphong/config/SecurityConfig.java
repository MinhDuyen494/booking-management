package com.minhduyen.quanlydatphong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security
public class SecurityConfig {

    // 1. Tạo Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng thuật toán mã hóa mạnh BCrypt
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình chuỗi bộ lọc bảo mật (Security Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì chúng ta dùng API stateless
            .authorizeHttpRequests(authorize -> authorize
                // Cho phép TẤT CẢ các request tới '/api/v1/auth/...' được truy cập mà không cần xác thực
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Tất cả các request còn lại đều phải được xác thực
                .anyRequest().authenticated()
            );

        return http.build();
    }
}