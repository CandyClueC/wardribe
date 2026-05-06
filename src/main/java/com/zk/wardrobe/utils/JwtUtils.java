package com.zk.wardrobe.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    private static final String SECRET = "e4b7f8a9d1c246e8b5a3f2d1c4e7b8a9f0d3c2b1"; // 生产环境请加密处理
    private static final long EXPIRE = 604800000; // 7天过期 (ms)

    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject("USER_INFO")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                // 【修复点 1】: 调用 getBytes() 转换为字节数组，阻止底层的错误解码
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 从 token 中解析出 userId
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                // 【修复点 2】: 解析时同样使用 getBytes()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.get("userId").toString());
    }
}