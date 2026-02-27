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
 * jwt令牌校验的拦截器（管理员端）
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

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

        String token = request.getHeader(jwtProperties.getAdminTokenName());

        try {
            log.info("jwt校验:{}", token);

            Claims claims = jwtUtil.parseAdminJWT(token);

            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：{}", empId);

            BaseContext.setCurrentId(empId);

            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败：{}", ex.getMessage());
            sendUnauthorizedResponse(response, "管理员登录已过期，请重新登录");
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