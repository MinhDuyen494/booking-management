package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.entity.User;
import com.minhduyen.quanlydatphong.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource; // Thêm import
import org.springframework.context.i18n.LocaleContextHolder; // Thêm import
import com.minhduyen.quanlydatphong.dto.*; // Import thêm
import jakarta.servlet.http.HttpServletRequest; // Thêm import



@RestController
@RequestMapping("/api/v1/auth") // Base path cho tất cả các API trong controller này
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource; // Inject MessageSource


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);

        // Lấy message từ file properties
        String successMessage = messageSource.getMessage(
            "auth.register.success", null, LocaleContextHolder.getLocale()
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(201)
                .message(successMessage) // Sử dụng message đã lấy
                .data(registeredUser)
                .build();

        return ResponseEntity.status(201).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .statusCode(200)
                .message("Login successful!")
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            authService.logout(token);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(200)
                .message("You have been logged out successfully.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}