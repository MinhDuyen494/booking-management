package com.minhduyen.quanlydatphong.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    // --- Đọc cấu hình từ application.properties ---
    @Value("${rate-limit.authenticated.capacity}")
    private int authCapacity;
    @Value("${rate-limit.authenticated.time}")
    private int authTime;
    @Value("${rate-limit.authenticated.unit}")
    private TimeUnit authUnit;

    @Value("${rate-limit.anonymous.capacity}")
    private int anonCapacity;
    @Value("${rate-limit.anonymous.time}")
    private int anonTime;
    @Value("${rate-limit.anonymous.unit}")
    private TimeUnit anonUnit;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String key = resolveKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, this::createNewBucket);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            String errorMessage = messageSource.getMessage(
                    "error.rate-limit.exceeded", null, LocaleContextHolder.getLocale());
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .statusCode(429)
                    .message(errorMessage)
                    .build();
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Nếu người dùng đã xác thực và không phải anonymous, dùng username làm key
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        // Nếu là người dùng ẩn danh, dùng địa chỉ IP làm key
        return request.getRemoteAddr();
    }

    private Bucket createNewBucket(String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Nếu người dùng đã xác thực, tạo bucket với giới hạn của người dùng đã xác
        // thực
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return buildBucket(authCapacity, authTime, authUnit);
        }
        // Nếu là người dùng ẩn danh, tạo bucket với giới hạn của người dùng ẩn danh
        return buildBucket(anonCapacity, anonTime, anonUnit);
    }

    private Bucket buildBucket(long capacity, long time, TimeUnit unit) {
        Refill refill = Refill.greedy(capacity, Duration.of(time, unit.toChronoUnit()));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}