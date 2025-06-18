package com.minhduyen.quanlydatphong.service;

import com.minhduyen.quanlydatphong.dto.RegisterRequest;
import com.minhduyen.quanlydatphong.entity.User;
import com.minhduyen.quanlydatphong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.minhduyen.quanlydatphong.dto.LoginRequest;
import com.minhduyen.quanlydatphong.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.minhduyen.quanlydatphong.entity.InvalidatedToken;
import com.minhduyen.quanlydatphong.repository.InvalidatedTokenRepository;
import java.util.Date;
import com.minhduyen.quanlydatphong.entity.Role;
import com.minhduyen.quanlydatphong.repository.RoleRepository;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import com.minhduyen.quanlydatphong.dto.ForgotPasswordRequest;
import com.minhduyen.quanlydatphong.dto.ResetPasswordRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import com.minhduyen.quanlydatphong.entity.ActiveToken;
import com.minhduyen.quanlydatphong.repository.ActiveTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor // Giúp tự động inject các dependency được khai báo final
public class AuthService {

    private final UserRepository userRepository;
    private final MessageSource messageSource; // Inject MessageSource
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final AuthenticationManager authenticationManager; // Inject AuthenticationManager
    private final JwtService jwtService; // Inject JwtService
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final RoleRepository roleRepository; // Inject RoleRepository
    private final ActiveTokenRepository activeTokenRepository;

    // Lấy giá trị từ file properties
    @Value("${app.security.max-concurrent-logins}")
    private int maxConcurrentLogins;

    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            String message = messageSource.getMessage("auth.register.username-exists", null, LocaleContextHolder.getLocale());
            throw new RuntimeException(message);
        }

        // Tìm vai trò ROLE_USER trong database
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("auth.role.not-found", null, LocaleContextHolder.getLocale())));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // 2. Tạo một đối tượng User mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());

        // Mã hóa mật khẩu của người dùng trước khi lưu vào database
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(roles); // <-- GÁN VAI TRÒ CHO USER MỚI

        // 3. Lưu người dùng mới vào cơ sở dữ liệu
        return userRepository.save(newUser);
    }
    // --- PHƯƠNG THỨC LOGIN ---
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
                .orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("auth.login.invalid", null, LocaleContextHolder.getLocale())));
        // Xử lý logic giới hạn đăng nhập đồng thời
        handleConcurrentLogins(user);
                // 3. Tạo JWT token
        var jwtToken = jwtService.generateToken(user);

       // Lưu token mới vào danh sách active
        ActiveToken activeToken = ActiveToken.builder()
                .id(jwtService.extractJti(jwtToken))
                .user(user)
                .expiryTime(jwtService.extractExpiration(jwtToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
        activeTokenRepository.save(activeToken);

        return LoginResponse.builder().accessToken(jwtToken).build();
    }

    private void handleConcurrentLogins(User user) {
    long activeTokenCount = activeTokenRepository.countByUserAndExpiryTimeAfter(user, LocalDateTime.now());

    Locale locale = LocaleContextHolder.getLocale();

    log.info(messageSource.getMessage("auth.concurrent-check.start",
            new Object[]{user.getUsername()}, locale));

    log.info(messageSource.getMessage("auth.concurrent-check.count",
            new Object[]{activeTokenCount}, locale));

    log.info(messageSource.getMessage("auth.concurrent-check.max",
            new Object[]{maxConcurrentLogins}, locale));

    if (activeTokenCount >= maxConcurrentLogins) {
        log.warn(messageSource.getMessage("auth.concurrent-check.limit-reached",
                new Object[]{user.getUsername()}, locale));

        activeTokenRepository.findFirstByUserOrderByCreatedAtAsc(user)
                .ifPresent(oldestToken -> {
                    log.info(messageSource.getMessage("auth.concurrent-check.oldest-token",
                            new Object[]{oldestToken.getId()}, locale));

                    Date expiry = Date.from(oldestToken.getExpiryTime().atZone(ZoneId.systemDefault()).toInstant());
                    invalidatedTokenRepository.save(new InvalidatedToken(oldestToken.getId(), expiry));
                    activeTokenRepository.delete(oldestToken);

                    log.info(messageSource.getMessage("auth.concurrent-check.oldest-token-removed",
                            null, locale));
                });
    } else {
        log.info(messageSource.getMessage("auth.concurrent-check.ok", null, locale));
    }
}

    //     // 4. Trả về token
    //     return LoginResponse.builder().accessToken(jwtToken).build();
    // }

    // PHƯƠNG THỨC LOGOUT ---
    public void logout(String token) {
        String jti = jwtService.extractJti(token);
        Date expiryTime = jwtService.extractExpiration(token);

        // Thêm vào blacklist
        invalidatedTokenRepository.save(new InvalidatedToken(jti, expiryTime));
        // Xóa khỏi danh sách active
        activeTokenRepository.deleteById(jti);
    }

    // PHƯƠNG THỨC FORGOT PASSWORD ---
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    String message = messageSource.getMessage(
                        "user.forgot-password.email-not-found", null, LocaleContextHolder.getLocale()
                    );
                    return new RuntimeException(message + ": " + request.getEmail());
                });
        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        // Đặt thời gian hết hạn (ví dụ: 15 phút)
        user.setResetTokenExpiryTime(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        // Tái sử dụng key message đã có cho việc kiểm tra mật khẩu không khớp
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            String message = messageSource.getMessage(
                "user.change-password.not-match", null, LocaleContextHolder.getLocale()
            );
            throw new IllegalStateException(message);
        }
        
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> {
                    String message = messageSource.getMessage(
                        "user.reset-password.invalid-token", null, LocaleContextHolder.getLocale()
                    );
                    return new RuntimeException(message);
                });

        if (user.getResetTokenExpiryTime().isBefore(LocalDateTime.now())) {
            String message = messageSource.getMessage(
                "user.reset-password.expired-token", null, LocaleContextHolder.getLocale()
            );
            throw new RuntimeException(message);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetTokenExpiryTime(null);
        userRepository.save(user);
    }
}