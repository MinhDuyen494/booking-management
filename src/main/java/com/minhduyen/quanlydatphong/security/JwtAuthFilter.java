package com.minhduyen.quanlydatphong.security;

import com.minhduyen.quanlydatphong.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.minhduyen.quanlydatphong.repository.InvalidatedTokenRepository;
import lombok.extern.slf4j.Slf4j; // <-- THÊM IMPORT NÀY


import java.io.IOException;

@Component // Đánh dấu là một Spring Bean để có thể inject ở nơi khác
@RequiredArgsConstructor
@Slf4j // <-- THÊM ANNOTATION NÀY ĐỂ DÙNG BIẾN 'log'
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final InvalidatedTokenRepository invalidatedTokenRepository; // <-- Inject repository


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain // Chuỗi các filter tiếp theo
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Kiểm tra xem header 'Authorization' có tồn tại và có bắt đầu bằng "Bearer " không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Nếu không có token, cho qua và xử lý tiếp
            return;
        }

        // 2. Tách lấy chuỗi token (bỏ đi 7 ký tự "Bearer ")
        jwt = authHeader.substring(7);

        // --- THÊM LOGIC KIỂM TRA BLACKLIST ---
        log.info("Received Token: {}", jwt); // Log để xem token nhận được

        try {
            final String jti = jwtService.extractJti(jwt);
            log.info("Extracted JTI from token: {}", jti); // Log để xem JTI trích xuất được

            boolean isInvalidated = invalidatedTokenRepository.existsById(jti);
            log.info("Checking blacklist for JTI {}. Is it invalidated? -> {}", jti, isInvalidated); // Log kết quả kiểm tra DB

            if (isInvalidated) {
                log.warn("TOKEN IS BLACKLISTED. Rejecting request for JTI: {}", jti);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been invalidated");
                return;
            }
        } catch (Exception e) {
            log.error("Invalid token processing", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        log.info("Token is valid, proceeding with authentication.");

        // 3. Trích xuất username từ token bằng JwtService
        username = jwtService.extractUsername(jwt);

        // 4. Kiểm tra username có tồn tại và người dùng chưa được xác thực trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin người dùng từ database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Nếu token hợp lệ, cập nhật SecurityContext
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Tạo một đối tượng xác thực
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Không cần credentials
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Lưu đối tượng xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Chuyển request và response cho filter tiếp theo trong chuỗi
        filterChain.doFilter(request, response);
    }
}