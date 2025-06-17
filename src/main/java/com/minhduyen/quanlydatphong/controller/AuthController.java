package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.entity.User;
import com.minhduyen.quanlydatphong.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.minhduyen.quanlydatphong.dto.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);
        String successMessage = messageSource.getMessage("auth.register.success", null, LocaleContextHolder.getLocale());
        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(201)
                .message(successMessage)
                .data(registeredUser)
                .build();
        return ResponseEntity.status(201).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        String loginSuccess = messageSource.getMessage("auth.login.success", null, LocaleContextHolder.getLocale());
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .statusCode(200)
                .message(loginSuccess)
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
        String logoutSuccess = messageSource.getMessage("auth.logout.success", null, LocaleContextHolder.getLocale());
        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(200)
                .message(logoutSuccess)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}