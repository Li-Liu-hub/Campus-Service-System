package com.jsyl.config;

import com.jsyl.constant.JwtClaimsConstant;
import com.jsyl.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            String uri = request.getURI().toString();
            log.info("WebSocket握手请求，URI：{}", uri);
            String token = extractToken(uri);
            log.info("提取到的Token：{}", token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

            if (token != null && !token.isEmpty()) {
                try {
                    Claims claims = jwtUtil.parseUserJWT(token);
                    Long userId = claims.get(JwtClaimsConstant.EMP_ID, Long.class);
                    if (userId == null) {
                        userId = claims.get(JwtClaimsConstant.USER_ID, Long.class);
                    }
                    if (userId != null) {
                        attributes.put("userId", userId);
                        log.info("WebSocket握手成功，用户ID：{}", userId);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败", e);
                }
            }
            log.warn("WebSocket握手失败：无效的token");
            return false;
        } catch (Exception e) {
            log.error("WebSocket握手异常", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手后异常", exception);
        }
    }

    private String extractToken(String uri) {
        if (uri.contains("token=")) {
            int startIndex = uri.indexOf("token=") + 6;
            int endIndex = uri.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = uri.length();
            }
            String encodedToken = uri.substring(startIndex, endIndex);
            try {
                return java.net.URLDecoder.decode(encodedToken, "UTF-8");
            } catch (Exception e) {
                log.warn("URL解码token失败", e);
                return encodedToken;
            }
        }
        return null;
    }

}
