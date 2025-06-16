package com.minhduyen.quanlydatphong.repository;

import com.minhduyen.quanlydatphong.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA sẽ tự động tạo ra câu lệnh query dựa trên tên của phương thức này
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

}