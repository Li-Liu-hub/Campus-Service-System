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
 * jwt令牌校验的拦截器（管理员端）
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    // 1. 注入Spring管理的JwtUtil Bean（核心修改：替换静态调用）
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
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌（使用配置中的adminTokenName，如"token"）
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);

            // 2.1 核心修改：调用JwtUtil的public方法parseAdminJWT，无需手动传secretKey
            // 该方法已封装了adminSecretKey的使用和非空校验
            Claims claims = jwtUtil.parseAdminJWT(token);

            // 2.2 解析员工ID，修复日志占位符问题（原代码缺少{}）
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：{}", empId);

            // 2.3 将员工ID存入上下文，供后续接口使用
            BaseContext.setCurrentId(empId);

            // 3、校验通过，放行
            return true;
        } catch (Exception ex) {
            // 4、校验不通过，响应401未授权状态码
            log.error("JWT校验失败：{}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}