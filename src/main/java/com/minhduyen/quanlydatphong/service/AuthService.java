package com.minhduyen.quanlydatphong.service;

import com.minhduyen.quanlydatphong.dto.RegisterRequest;
import com.minhduyen.quanlydatphong.entity.User;
import com.minhduyen.quanlydatphong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource; // Thêm import
import org.springframework.context.i18n.LocaleContextHolder; // Thêm import
import org.springframework.security.crypto.password.PasswordEncoder; // Thêm import
import com.minhduyen.quanlydatphong.dto.LoginRequest;
import com.minhduyen.quanlydatphong.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Service
@RequiredArgsConstructor // Giúp tự động inject các dependency được khai báo final
public class AuthService {

    private final UserRepository userRepository;
    private final MessageSource messageSource; // Inject MessageSource
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final AuthenticationManager authenticationManager; // Inject AuthenticationManager
    private final JwtService jwtService; // Inject JwtService


    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            // Thay thế chuỗi cứng bằng cách lấy message từ file properties
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            String message = messageSource.getMessage(
                "auth.register.username-exists", null, LocaleContextHolder.getLocale()
            );
            throw new RuntimeException(message);
        }
        }

        // 2. Tạo một đối tượng User mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());

        // Mã hóa mật khẩu của người dùng trước khi lưu vào database
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        // 3. Lưu người dùng mới vào cơ sở dữ liệu
        return userRepository.save(newUser);
    }

    public LoginResponse login(LoginRequest request) {
        // 1. Xác thực người dùng bằng AuthenticationManager
        // Nó sẽ tự động gọi CustomUserDetailsService và dùng PasswordEncoder để kiểm tra
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Nếu xác thực thành công, tìm lại thông tin user
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // 3. Tạo JWT token
        var jwtToken = jwtService.generateToken(user);

        // 4. Trả về token
        return LoginResponse.builder().accessToken(jwtToken).build();
    }
}