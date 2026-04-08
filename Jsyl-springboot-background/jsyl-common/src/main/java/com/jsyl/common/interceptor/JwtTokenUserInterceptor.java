package com.jsyl.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsyl.common.constant.JwtClaimsConstant;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.properties.JwtProperties;
import com.jsyl.common.result.Result;
import com.jsyl.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtil jwtUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = null;

        // 1. 尝试从配置的token名称获取
        String userTokenName = jwtProperties.getUserTokenName();
        if (userTokenName != null) {
            token = request.getHeader(userTokenName);
        }

        // 2. 尝试从标准的Authorization请求头获取（支持Bearer格式）
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // 移除 "Bearer " 前缀
            }
        }

        // 3. 尝试从token请求头获取
        if (token == null) {
            token = request.getHeader("token");
        }


        try {

            if (token == null || token.trim().isEmpty()) {
                log.error("JWT令牌为空");
                sendUnauthorizedResponse(response, "请先登录");
                return false;
            }

            Claims claims = jwtUtil.parseUserJWT(token);

            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());

            BaseContext.setCurrentId(userId);

            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败：{}", ex.getMessage());
            sendUnauthorizedResponse(response, "登录已过期，请重新登录");
            return false;
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        Result<String> result = Result.error(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
