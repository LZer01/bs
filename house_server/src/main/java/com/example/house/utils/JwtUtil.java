//package com.example.house.utils;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpirationInMs;
//
//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());  // 推荐 HS512
//    }
//
//    // 生成 Token（登录成功后调用）
//    public String generateToken(Authentication authentication) {
//        String username = authentication.getName();
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(now)
//                .expiration(expiryDate)
//                .signWith(getSigningKey(), Jwts.SIG.HS512)  // 或 HS256
//                .compact();
//    }
//
//    // 从 Token 中获取用户名
//    public String getUsernameFromJWT(String token) {
//        Claims claims = Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claims.getSubject();
//    }
//
//    // 验证 Token 是否有效
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
//            return true;
//        } catch (Exception ex) {
//            // 可以记录日志：过期、签名错误等
//            return false;
//        }
//    }
//}