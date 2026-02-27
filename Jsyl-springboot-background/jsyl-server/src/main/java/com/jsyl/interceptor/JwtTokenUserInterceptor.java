package com.jsyl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsyl.constant.JwtClaimsConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.properties.JwtProperties;
import com.jsyl.result.Result;
import com.jsyl.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt令牌校验的拦截器（用户端）
 */
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
        String userTokenName = jwtProperties.getUserTokenName();
        if (userTokenName != null) {
            token = request.getHeader(userTokenName);
        }
        if (token == null) {
            token = request.getHeader("token");
        }

        try {
            log.info("jwt校验:{}", token);

            if (token == null || token.trim().isEmpty()) {
                log.error("JWT令牌为空");
                sendUnauthorizedResponse(response, "请先登录");
                return false;
            }

            Claims claims = jwtUtil.parseUserJWT(token);

            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);

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
