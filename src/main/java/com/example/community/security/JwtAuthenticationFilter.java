package com.example.community.security;

import com.example.community.entity.User;
import com.example.community.exception.ErrorCode;
import com.example.community.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 로그인이면 인증 건너뛰기
        if (requestURI.equals("/api/users/token") && method.equalsIgnoreCase("POST")) {
            chain.doFilter(request, response);
            return;
        }
        // 회원가입이면 인증 건너뛰기
        if (requestURI.equals("/api/users")) {
            chain.doFilter(request, response);
            return;
        }

        // 쿠키에서 토큰 가져오기
        String accessToken = getTokenFromCookies(request, "accessToken");
        String refreshToken = getTokenFromCookies(request, "refreshToken");

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            // 엑세스 토큰이 존재하고, 유효한 경우 -> 사용자 인증하기
            authenticateUser(accessToken, request);
        } else if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            // Access Token이 만료되었고, Refresh Token이 유효하면 새로운 Token Pair 발급
            Long userId = jwtUtil.extractUserId(refreshToken);

            String newAccessToken = jwtUtil.generateAccessToken(userId);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId);

            response.addCookie(jwtUtil.createTokenCookie("accessToken", newAccessToken, JwtUtil.getAccessExpirationTime()));
            response.addCookie(jwtUtil.createTokenCookie("refreshToken", newRefreshToken, JwtUtil.getRefreshExpirationTime()));

            authenticateUser(newAccessToken, request);
        } else {
            handleException(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        chain.doFilter(request, response);
    }

    // Security Context에 사용자 정보 저장
    private void authenticateUser(String token, HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String getTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{ \"message\": \"" + errorCode.getMessage() + "\" }");
    }
}
