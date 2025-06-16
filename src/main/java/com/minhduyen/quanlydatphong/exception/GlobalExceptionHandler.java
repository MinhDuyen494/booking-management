package com.minhduyen.quanlydatphong.exception;

import com.minhduyen.quanlydatphong.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice: Đánh dấu lớp này là một trình xử lý ngoại lệ toàn cục.
// Spring sẽ quét và đăng ký nó.
@ControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler: Đánh dấu phương thức này sẽ xử lý ngoại lệ 'Exception.class'
    // 'Exception.class' là lớp cha của hầu hết các ngoại lệ, nên nó sẽ bắt được gần như mọi lỗi.
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(500); // 500: Internal Server Error
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(500).body(apiResponse);
    }
}