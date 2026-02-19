package com.jsyl.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // 注入你的yml配置（管理员+用户）
    @Value("${jsyl.jwt.admin-secret-key}")
    private String adminSecretKey;
    @Value("${jsyl.jwt.admin-ttl}")
    private long adminTtl;
    @Value("${jsyl.jwt.user-secret-key}")
    private String userSecretKey;
    @Value("${jsyl.jwt.user-ttl}")
    private long userTtl;

    // 启动时校验配置，提前发现问题
    @PostConstruct
    public void checkConfig() {
        validateSecretKey(adminSecretKey, "管理员");
        validateSecretKey(userSecretKey, "普通用户");
        validateTtl(adminTtl, "管理员");
        validateTtl(userTtl, "普通用户");
    }

    /**
     * 生成管理员JWT
     */
    public String createAdminJWT(Map<String, Object> claims) {
        return createJWT(adminSecretKey, adminTtl, claims);
    }

    /**
     * 生成普通用户JWT
     */
    public String createUserJWT(Map<String, Object> claims) {
        return createJWT(userSecretKey, userTtl, claims);
    }

    /**
     * 解析管理员JWT
     */
    public Claims parseAdminJWT(String token) {
        return parseJWT(adminSecretKey, token);
    }

    /**
     * 解析普通用户JWT
     */
    public Claims parseUserJWT(String token) {
        return parseJWT(userSecretKey, token);
    }

    /**
     * 核心生成JWT方法（适配JJWT 0.9.1 API）
     */
    private String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 入参校验
        validateSecretKey(secretKey, "");
        validateTtl(ttlMillis, "");
        if (claims == null) {
            throw new IllegalArgumentException("JWT载荷（claims）不能为空！");
        }

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                // 适配0.9.1版本的signWith方法（无Keys类）
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * 核心解析JWT方法（适配JJWT 0.9.1 API）
     */
    private Claims parseJWT(String secretKey, String token) {
        // 入参校验
        validateSecretKey(secretKey, "");
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT令牌（token）不能为空！");
        }

        try {
            // 0.9.1版本使用parser()而非parserBuilder()
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
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