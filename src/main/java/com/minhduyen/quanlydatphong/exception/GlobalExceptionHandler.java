package com.minhduyen.quanlydatphong.exception;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice: Đánh dấu lớp này là một trình xử lý ngoại lệ toàn cục.
// Spring sẽ quét và đăng ký nó.
@ControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    // @ExceptionHandler: Đánh dấu phương thức này sẽ xử lý ngoại lệ 'Exception.class'
    // 'Exception.class' là lớp cha của hầu hết các ngoại lệ, nên nó sẽ bắt được gần như mọi lỗi.
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(500); // 500: Internal Server Error
        String msg = messageSource.getMessage("exception.internal-server-error", null, LocaleContextHolder.getLocale());
        apiResponse.setMessage(msg + ": " + exception.getMessage());

        return ResponseEntity.status(500).body(apiResponse);
    }
}