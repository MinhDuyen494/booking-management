package com.minhduyen.quanlydatphong.service;

import com.minhduyen.quanlydatphong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Phương thức này được Spring Security gọi khi một người dùng cố gắng xác thực.
     * @param username Tên đăng nhập mà người dùng nhập vào.
     * @return một đối tượng UserDetails (chính là lớp User của chúng ta).
     * @throws UsernameNotFoundException nếu không tìm thấy người dùng.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}