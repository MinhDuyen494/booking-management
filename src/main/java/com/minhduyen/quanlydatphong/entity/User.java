package com.minhduyen.quanlydatphong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity // Đánh dấu đây là một Entity, sẽ được ánh xạ tới một bảng trong DB
@Table(name = "users") // Tên của bảng trong DB, thường dùng số nhiều
public class User extends BaseEntity { // Kế thừa từ BaseEntity để có các trường chung

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

}