package com.minhduyen.quanlydatphong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Thêm import này

@SpringBootApplication
@EnableJpaAuditing // <-- Thêm annotation này
public class QuanlydatphongApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanlydatphongApplication.class, args);
    }

}