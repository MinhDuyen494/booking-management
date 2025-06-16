package com.minhduyen.quanlydatphong.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: ROLE_ADMIN, ROLE_USER

    // fetch = FetchType.EAGER: Khi tải một Role, tải luôn các Permission của nó.
    // Điều này tiện lợi cho việc kiểm tra quyền.
    @ManyToMany(fetch = FetchType.EAGER)
    // @JoinTable: Định nghĩa bảng trung gian để nối giữa roles và permissions
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}