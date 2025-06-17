package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    // Lấy giá trị từ file application.properties
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getAppInfo() {
        Map<String, String> appInfo = Map.of(
            "appName", appName,
            "version", "1.0.0",
            "author", "Minh Duyen"
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(200)
                .message("Application information retrieved successfully.")
                .data(appInfo)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

