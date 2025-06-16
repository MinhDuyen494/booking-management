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
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: USER_CREATE, USER_READ, USER_UPDATE

    private String description;
}