package com.minhduyen.quanlydatphong.config;

import com.minhduyen.quanlydatphong.security.JwtAuthFilter; // Thêm import
import com.minhduyen.quanlydatphong.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy; // Thêm import
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Thêm import

@Configuration
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security
@RequiredArgsConstructor // Dùng constructor injection
@EnableMethodSecurity // <-- Kích hoạt phân quyền trên method
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter; // <-- Inject JwtAuthFilter


    // 1. Tạo Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng thuật toán mã hóa mạnh BCrypt
        return new BCryptPasswordEncoder();
    }
    /* Bean này quản lý quá trình xác thực. Chúng ta sẽ dùng nó trong API đăng nhập. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 2. Cấu hình chuỗi bộ lọc bảo mật (Security Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider())
            // Cấu hình session management là STATELESS (quan trọng cho JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**", "/api/v1/public/**").permitAll()
                .anyRequest().authenticated()
            )
            // Thêm JwtAuthFilter của chúng ta vào trước bộ lọc UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
}