package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    private final MessageSource messageSource;

    public PublicController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getAppInfo() {
        Map<String, String> appInfo = Map.of(
            "appName", appName,
            "version", "1.0.0",
            "author", "Minh Duyen"
        );

        String infoMsg = messageSource.getMessage("public.info.success", null, LocaleContextHolder.getLocale());

        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(200)
                .message(infoMsg)
                .data(appInfo)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

