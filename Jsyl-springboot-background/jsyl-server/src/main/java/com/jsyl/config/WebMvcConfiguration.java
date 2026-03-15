package com.jsyl.config;

import com.jsyl.interceptor.JwtTokenAdminInterceptor;
import com.jsyl.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    protected void addInterceptors(InterceptorRegistry registry) {

        // 管理端拦截器
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .addPathPatterns("/jsyl/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/jsyl/admin/login");

        // 用户端拦截器
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/jsyl/user/**")
                .addPathPatterns("/jsyl/home/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/jsyl/user/user/login")
                .excludePathPatterns("/jsyl/user/login")
                .excludePathPatterns("/jsyl/user/register")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/jsyl/common/login")
                .excludePathPatterns("/jsyl/common/register")
                .excludePathPatterns("/jsyl/common/campusList")
                // 排除管理员相关路径，避免被用户拦截器拦截
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/jsyl/admin/**");
    }
}
