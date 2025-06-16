package com.minhduyen.quanlydatphong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    // Có thể thêm refreshToken ở đây trong tương lai
    // private String refreshToken;
}