package com.minhduyen.quanlydatphong.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InvalidatedToken {
    @Id
    private String id; // Lưu JTI của token
    private Date expiryTime; // Lưu thời gian hết hạn để có thể dọn dẹp sau này
}