package com.example.community.security;

import com.example.community.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final String JWT_SECRET_KEY;
    private static final int ACCESS_EXPIRATION_TIME = 60 * 30;  // Access Token: 30분
    private static final int REFRESH_EXPIRATION_TIME = 60 * 60 * 24 * 7;  // Refresh Token: 7일

    public static int getAccessExpirationTime() {
        return ACCESS_EXPIRATION_TIME;
    }

    public static int getRefreshExpirationTime() {
        return REFRESH_EXPIRATION_TIME;
    }

    public JwtUtil(@Value("${JWT_SECRET_KEY}") String jwtSecretKey) {
        this.JWT_SECRET_KEY = jwtSecretKey;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 발급
    public String generateAccessToken(Long userId) {
        return generateToken(userId, ACCESS_EXPIRATION_TIME);
    }

    // Refresh Token 발급
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, REFRESH_EXPIRATION_TIME);
    }

    // JWT 생성 로직
    private String generateToken(Long userId, long expirationTime) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException e) {
            log.warn("Invalid JWT Token: {}", token);
            return null;
        }
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 현재 로그인된 사용자 정보 가져오기
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    // 토큰 쿠키 추가
    public void addTokenCookie(HttpServletResponse response, String name, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 토큰 쿠키 삭제
    public void deleteTokenCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
