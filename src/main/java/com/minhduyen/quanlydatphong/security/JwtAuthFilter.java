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

import java.io.IOException;

@Component // Đánh dấu là một Spring Bean để có thể inject ở nơi khác
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

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