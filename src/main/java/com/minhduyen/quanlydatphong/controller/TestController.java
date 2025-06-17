package com.minhduyen.quanlydatphong.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test") // Một đường dẫn mới, không có trong danh sách permitAll
public class TestController {
    private final MessageSource messageSource;

    public TestController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/hello-user")
    public ResponseEntity<String> helloUser() {
        String helloMsg = messageSource.getMessage("test.hello-user", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(helloMsg);
    }

}