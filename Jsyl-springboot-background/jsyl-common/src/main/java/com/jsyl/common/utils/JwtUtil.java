package com.jsyl.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // 注入你的yml配置
    @Value("${jsyl.jwt.user-secret-key}")
    private String userSecretKey;
    @Value("${jsyl.jwt.user-ttl}")
    private long userTtl;

    // 启动时校验配置，提前发现问题
    @PostConstruct
    public void checkConfig() {
        validateSecretKey(userSecretKey, "普通用户");
        validateTtl(userTtl, "普通用户");
    }


    public String createUserJWT(Map<String, Object> claims) {
        return createJWT(userSecretKey, userTtl, claims);
    }


    public Claims parseUserJWT(String token) {
        return parseJWT(userSecretKey, token);
    }

    private String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 入参校验
        validateSecretKey(secretKey, "");
        validateTtl(ttlMillis, "");
        if (claims == null) {
            throw new IllegalArgumentException("JWT载荷（claims）不能为空！");
        }

        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .claims(claims)
                .expiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private Claims parseJWT(String secretKey, String token) {
        // 入参校验
        validateSecretKey(secretKey, "");
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT令牌（token）不能为空！");
        }

        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("JWT解析失败：" + e.getMessage(), e);
        }
    }

    // 校验秘钥非空
    private void validateSecretKey(String secretKey, String type) {
        String prefix = type.isEmpty() ? "" : type + " ";
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException(prefix + "JWT秘钥（secretKey）不能为空！");
        }
    }

    // 校验过期时间合法
    private void validateTtl(long ttlMillis, String type) {
        String prefix = type.isEmpty() ? "" : type + " ";
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException(prefix + "JWT过期时间（ttlMillis）必须大于0！");
        }
    }
}
