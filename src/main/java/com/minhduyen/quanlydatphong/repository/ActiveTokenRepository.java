package com.minhduyen.quanlydatphong.repository;

import com.minhduyen.quanlydatphong.entity.ActiveToken;
import com.minhduyen.quanlydatphong.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActiveTokenRepository extends JpaRepository<ActiveToken, String> {
    // Đếm số token đang hoạt động của một user
    long countByUserAndExpiryTimeAfter(User user, LocalDateTime currentTime);

    // Tìm token cũ nhất của một user
    Optional<ActiveToken> findFirstByUserOrderByCreatedAtAsc(User user);

    // Tìm tất cả các token đang hoạt động của user
    List<ActiveToken> findByUser(User user);
}