package com.minhduyen.quanlydatphong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass // Đánh dấu đây là lớp cha, các thuộc tính của nó sẽ được ánh xạ vào các cột của entity con
@EntityListeners(AuditingEntityListener.class) // Kích hoạt tính năng tự động điền ngày tháng
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate // Tự động lấy ngày giờ hiện tại khi một record được tạo
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Tự động lấy ngày giờ hiện tại khi một record được cập nhật
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    // Chúng ta sẽ thêm người tạo/người cập nhật ở các bước sau khi có hệ thống xác thực người dùng
    // private String createdBy;
    // private String updatedBy;
}