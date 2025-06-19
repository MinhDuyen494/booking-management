package com.minhduyen.quanlydatphong.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.time.LocalDateTime; // Thêm import

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity // Đánh dấu đây là một Entity, sẽ được ánh xạ tới một bảng trong DB
@Table(name = "users") // Tên của bảng trong DB, thường dùng số nhiều
public class User extends BaseEntity implements UserDetails { // Kế thừa từ BaseEntity để có các trường chung

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    private String resetPasswordToken;
    private LocalDateTime resetTokenExpiryTime;

    /**
     * Phương thức quan trọng nhất: Trả về danh sách các quyền (Permissions) của
     * người dùng.
     * Spring Security sẽ dùng danh sách này để kiểm tra quyền truy cập
     * (Authorization).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Từ danh sách các Role, ta lấy ra tất cả các Permission
        // và chuyển chúng thành các đối tượng SimpleGrantedAuthority.
        java.util.List<GrantedAuthority> authorities = this.roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(java.util.stream.Collectors.toList());

        // Thêm các role (ví dụ: "ROLE_USER") vào danh sách authorities
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return authorities;
    }

    // Phương thức getPassword() và getUsername() đã được Lombok (@Getter) tạo sẵn.

    @Override
    public boolean isAccountNonExpired() {
        return true; // tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // mật khẩu không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return true; // tài khoản được kích hoạt
    }

}