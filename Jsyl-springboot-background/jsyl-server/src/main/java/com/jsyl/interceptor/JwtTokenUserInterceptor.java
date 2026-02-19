package com.jsyl.interceptor;

import com.jsyl.constant.JwtClaimsConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.properties.JwtProperties;
import com.jsyl.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
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
                response.setStatus(401);
                return false;
            }

            Claims claims = jwtUtil.parseUserJWT(token);

            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);

            BaseContext.setCurrentId(userId);

            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败：{}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
