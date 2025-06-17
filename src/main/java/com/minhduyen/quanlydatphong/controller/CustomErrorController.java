package com.minhduyen.quanlydatphong.controller;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Chú ý: Dùng @Controller, không phải @RestController
public class CustomErrorController implements ErrorController {
    private final MessageSource messageSource;

    public CustomErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse> handleError(HttpServletRequest request) {
        // Lấy mã trạng thái lỗi gốc (ví dụ: 404, 500)
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(); // Mặc định là lỗi 500

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        String message;
        // Tạo thông điệp thân thiện dựa trên mã lỗi
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            message = messageSource.getMessage("error.404", null, LocaleContextHolder.getLocale());
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            message = messageSource.getMessage("error.500", null, LocaleContextHolder.getLocale());
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            message = messageSource.getMessage("error.403", null, LocaleContextHolder.getLocale());
        } else {
            message = messageSource.getMessage("error.unknown", null, LocaleContextHolder.getLocale());
        }

        // Xây dựng đối tượng ApiResponse chuẩn của chúng ta
        ApiResponse apiResponse = ApiResponse.builder()
                .statusCode(statusCode)
                .message(message)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(statusCode));
    }
}