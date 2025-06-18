package com.minhduyen.quanlydatphong.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmationPassword;
}