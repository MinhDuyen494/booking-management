package com.minhduyen.quanlydatphong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test") // Một đường dẫn mới, không có trong danh sách permitAll
public class TestController {

    @GetMapping("/hello-user")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello User! Bạn đã truy cập thành công vào API được bảo vệ.");
    }

}