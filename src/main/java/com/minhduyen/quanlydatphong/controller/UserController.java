package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import com.minhduyen.quanlydatphong.dto.ChangePasswordRequest;
import com.minhduyen.quanlydatphong.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users") // Base path cho các API liên quan đến user
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final MessageSource messageSource;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser // Spring sẽ tự động inject Principal của user đã xác thực
    ) {
        service.changePassword(request, connectedUser);
        String msg = messageSource.getMessage("user.change-password.success", null, LocaleContextHolder.getLocale());
        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(200)
                .message(msg)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}