# SpringDoc OpenAPI 集成指南

## 目标
为 Spring Boot 3.x 项目集成 SpringDoc OpenAPI，自动生成 API 文档，支持 Swagger UI 访问和 Apifox 导入。

## 1. 添加依赖

pom.xml 添加：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

## 2. application.yml 配置

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
```

## 3. OpenAPI 配置类

创建 `config/OpenApiConfig.java`：

```java
package com.jsyl.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园服务平台 API")
                        .version("1.0")
                        .description("校园服务平台后端接口文档")
                        .contact(new Contact().name("JSYL")))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }
}
```

## 4. Spring Security 放行

在 SecurityConfig 中放行 SpringDoc 相关路径：

```java
// 在 SecurityFilterChain 的 authorizeHttpRequests 中添加：
.requestMatchers(
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html"
).permitAll()
```

同时如果项目有自定义拦截器（WebMvcConfiguration 中的 addInterceptors），也要排除这些路径：

```java
.excludePathPatterns(
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html"
)
```

## 5. Controller 注解（可选但推荐）

给 Controller 和方法加上文档注解，让生成的文档更清晰：

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI服务", description = "AI相关接口")
@RestController
@RequestMapping("/ai")
public class AiTestController {

    @Operation(summary = "AI对话", description = "发送消息给AI并获取回复")
    @GetMapping("/chat")
    public Result<String> chat(
            @Parameter(description = "用户消息") @RequestParam(defaultValue = "你好") String message) {
        // ...
    }
}
```

## 6. 验证

启动项目后访问：
- Swagger UI: http://localhost:8080/swagger-ui.html
- API JSON: http://localhost:8080/v3/api-docs

## 7. Apifox 导入

Apifox → 项目设置 → 导入数据 → URL 导入 → 填入 `http://localhost:8080/v3/api-docs`

## 注意事项

- 项目用的是 Spring Boot 3.x + Jakarta，必须用 `springdoc-openapi-starter-webmvc-ui`（不是 springfox 或旧版 springdoc）
- 如果项目有 context-path，访问地址要加上前缀
- JWT token 在 Swagger UI 右上角 Authorize 按钮输入，格式为 Bearer {token}
- 生产环境建议通过 profile 关闭：`springdoc.api-docs.enabled=false`
