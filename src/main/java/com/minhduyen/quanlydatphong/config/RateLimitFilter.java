package com.minhduyen.quanlydatphong.config; // Hoặc package bạn đang đặt file này

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhduyen.quanlydatphong.dto.ApiResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor // Sử dụng Lombok để tự động inject các dependency
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final MessageSource messageSource; // Inject MessageSource
    private final ObjectMapper objectMapper;   // Inject ObjectMapper để chuyển đối tượng thành JSON

    // Cấu hình mỗi IP chỉ được 1 request mỗi 10 giây
    private Bucket createNewBucket() {
        // Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(10)));
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String key = resolveKey(request); // Dựa theo IP
        Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // --- PHẦN RESPONSE ĐÃ ĐƯỢC SỬA LẠI ---

            // 1. Lấy thông điệp lỗi từ file properties
            String errorMessage = messageSource.getMessage(
                    "error.rate-limit.exceeded", null, LocaleContextHolder.getLocale()
            );

            // 2. Tạo đối tượng ApiResponse chuẩn của chúng ta
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .statusCode(429)
                    .message(errorMessage)
                    .build();

            // 3. Thiết lập status và content type cho response
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");

            // 4. Dùng ObjectMapper để chuyển đổi object thành chuỗi JSON và ghi vào response
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
    }

    private String resolveKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}