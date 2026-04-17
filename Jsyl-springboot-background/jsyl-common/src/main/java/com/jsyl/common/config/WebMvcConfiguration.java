package com.jsyl.common.config;

import com.jsyl.common.interceptor.JwtTokenUserInterceptor;
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


        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/jsyl/user/**")
                .addPathPatterns("/jsyl/home/**")
                .addPathPatterns("/admin/**")
                .addPathPatterns("/jsyl/admin/**")
                .addPathPatterns("/ai/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/jsyl/user/user/login")
                .excludePathPatterns("/jsyl/user/login")
                .excludePathPatterns("/jsyl/user/register")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/jsyl/common/login")
                .excludePathPatterns("/jsyl/common/register")
                .excludePathPatterns("/jsyl/common/campusList")
                // 排除 AI 测试接口，方便开发调试
                // 排除 SpringDoc OpenAPI 文档相关路径
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/swagger-ui.html");
    }
}
