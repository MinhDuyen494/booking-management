package com.minhduyen.quanlydatphong.config;

import com.minhduyen.quanlydatphong.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @org.springframework.lang.NonNull
    public Optional<String> getCurrentAuditor() {
        // Lấy thông tin xác thực từ SecurityContext của Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            // Nếu là hành động không cần đăng nhập (ví dụ: tự đăng ký tài khoản)
            // Bạn có thể trả về một giá trị mặc định
            return Optional.of("system");
        }

        // Lấy username của người dùng đã đăng nhập
        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getUsername());
    }
}