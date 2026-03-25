package com.anime.shop.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // 保留你原有密钥和过期时间配置
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION = 86400000; // 24小时过期

    /**
     * 原有方法：仅生成带userId的Token（兼容原有调用）
     * @param userId 用户ID
     * @return Token字符串
     */
    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 新增核心方法：生成带userId+role的Token（登录接口使用）
     * @param userId 用户ID
     * @param role 用户角色（admin/manager/user）
     * @return Token字符串
     */
    public String generateToken(Long userId, String role) {
        // 构建自定义Claims，嵌入role字段（核心：前端无法篡改）
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // 存储真实角色
        claims.put("userId", userId); // 冗余存储userId，方便解析

        return Jwts.builder()
                .setClaims(claims) // 设置自定义Claims
                .subject(userId.toString()) // 保留原有subject
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 原有方法：从Token解析userId（兼容原有调用）
     * @param token Token字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            // 优先从自定义Claims解析，兼容原有逻辑
            Claims claims = parseTokenClaims(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj != null) {
                return Long.parseLong(userIdObj.toString());
            }
            // 兼容原有subject存储逻辑
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 新增核心方法：从Token解析真实role（后端权限校验用）
     * @param token Token字符串
     * @return 用户角色（admin/manager/user）
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = parseTokenClaims(token);
            return claims.get("role", String.class); // 从Claims中获取role
        } catch (Exception e) {
            return null; // 解析失败返回null，视为无权限
        }
    }

    /**
     * 新增方法：校验Token是否过期（拦截器用）
     * @param token Token字符串
     * @return true=过期，false=有效
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseTokenClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // 解析失败视为过期
        }
    }

    /**
     * 原有方法：校验Token是否有效（兼容原有调用）
     * @param token Token字符串
     * @return true=有效，false=无效
     */
    public boolean validateToken(String token) {
        try {
            parseTokenClaims(token); // 解析成功即有效
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 内部工具方法：统一解析Token的Claims
     * @param token Token字符串
     * @return Claims对象
     * @throws JwtException 解析失败抛出异常
     */
    private Claims parseTokenClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}