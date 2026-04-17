# AI 智能内容审核模块 - 开发文档

## 项目背景

校园服务平台（jsyl）需要在发帖时增加 AI 内容审核功能，作为现有 Trie 树关键词过滤的第二道防线。使用 LangChain4j 框架对接大模型 API，实现语义级别的内容审核。

## 项目结构

这是一个 Maven 多模块 Spring Boot 3.2.5 项目，Java 17+：

```
jsyl-common/    → 通用工具、配置、注解、异常
jsyl-pojo/      → DTO、Entity、VO
jsyl-server/    → Controller、Service、Mapper、业务逻辑
```

新增的 AI 模块代码全部放在 `jsyl-server` 中。

## 一、依赖配置

在 `jsyl-server/pom.xml` 的 `<dependencies>` 中添加：

```xml
<!-- LangChain4j Spring Boot 核心启动器 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-spring-boot-starter</artifactId>
    <version>1.0.0-beta3</version>
</dependency>

<!-- LangChain4j OpenAI 集成启动器（兼容所有 OpenAI 格式的 API） -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>1.0.0-beta3</version>
</dependency>
```

## 二、YAML 配置

在 `jsyl-server/src/main/resources/application-dev.yml` 中追加以下配置：

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${AI_API_KEY:demo}
      model-name: gpt-4o-mini
      base-url: http://langchain4j.dev/demo/openai/v1
      log-requests: true
      log-responses: true
```

说明：`demo` 是 LangChain4j 官方提供的免费测试 key，开发阶段可直接使用。生产环境需替换为真实 API key。

## 三、新建文件清单

### 3.1 AI 服务接口

**文件路径**：`jsyl-server/src/main/java/com/jsyl/module/ai/service/ContentModerationAi.java`

```java
package com.jsyl.module.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * AI 内容审核服务
 * 使用 LangChain4j 的声明式 AI 服务，框架自动生成实现类并注册为 Spring Bean
 */
@AiService
public interface ContentModerationAi {

    @SystemMessage("""
        你是一个内容审核助手，负责审核校园服务平台的用户发布内容。
        判断内容是否包含以下违规信息：色情、暴力、广告引流、政治敏感、人身攻击、违法信息。
        必须且只能返回如下JSON格式，不要返回任何其他内容：
        {"pass": true, "reason": "审核通过"}
        或
        {"pass": false, "reason": "具体违规原因"}
        """)
    @UserMessage("请审核以下内容：\n标题：{{title}}\n正文：{{content}}")
    String moderate(@V("title") String title, @V("content") String content);
}
```

### 3.2 AI 审核业务封装 Service

**文件路径**：`jsyl-server/src/main/java/com/jsyl/module/ai/service/AiModerationService.java`

```java
package com.jsyl.module.ai.service;

/**
 * AI 审核业务封装接口
 */
public interface AiModerationService {

    /**
     * 审核帖子内容
     * @param title 帖子标题
     * @param content 帖子正文
     * @throws IllegalArgumentException 如果内容审核不通过
     */
    void moderatePost(String title, String content);
}
```

### 3.3 AI 审核 Service 实现

**文件路径**：`jsyl-server/src/main/java/com/jsyl/module/ai/service/impl/AiModerationServiceImpl.java`

```java
package com.jsyl.module.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsyl.module.ai.service.AiModerationService;
import com.jsyl.module.ai.service.ContentModerationAi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiModerationServiceImpl implements AiModerationService {

    @Autowired
    private ContentModerationAi contentModerationAi;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void moderatePost(String title, String content) {
        try {
            String result = contentModerationAi.moderate(title, content);
            log.info("AI审核结果：{}", result);

            JsonNode json = objectMapper.readTree(result);
            JsonNode passNode = json.get("pass");

            if (passNode != null && !passNode.asBoolean()) {
                String reason = json.has("reason") ? json.get("reason").asText() : "内容不合规";
                throw new IllegalArgumentException("AI内容审核不通过：" + reason);
            }
        } catch (IllegalArgumentException e) {
            // 审核不通过，直接往上抛
            throw e;
        } catch (Exception e) {
            // AI 服务异常时降级处理，不阻塞用户发帖
            log.warn("AI审核服务异常，跳过AI审核：{}", e.getMessage());
        }
    }
}
```

### 3.4 AI 测试 Controller

**文件路径**：`jsyl-server/src/main/java/com/jsyl/module/ai/controller/AiTestController.java`

```java
package com.jsyl.module.ai.controller;

import com.jsyl.common.result.Result;
import com.jsyl.module.ai.service.AiModerationService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@Api(tags = "AI 功能测试接口")
@Slf4j
public class AiTestController {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private AiModerationService aiModerationService;

    @GetMapping("/chat")
    @ApiOperation("AI 聊天测试")
    public Result<String> chat(@RequestParam(defaultValue = "你好") String message) {
        String answer = chatLanguageModel.chat(message);
        return Result.success(answer);
    }

    @PostMapping("/moderate")
    @ApiOperation("AI 内容审核测试")
    public Result<Map<String, Object>> moderate(@RequestParam String title,
                                                 @RequestParam String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            aiModerationService.moderatePost(title, content);
            result.put("pass", true);
            result.put("reason", "审核通过");
        } catch (IllegalArgumentException e) {
            result.put("pass", false);
            result.put("reason", e.getMessage());
        }
        return Result.success(result);
    }
}
```

## 四、修改现有文件

### 4.1 修改 PostServiceImpl —— 在发帖流程中接入 AI 审核

**文件路径**：`jsyl-server/src/main/java/com/jsyl/module/forum/service/impl/PostServiceImpl.java`

在现有的 `publish` 方法中，在关键词过滤之后、构建 Post 对象之前，增加 AI 审核调用：

1. 注入 `AiModerationService`：

```java
@Autowired
private AiModerationService aiModerationService;
```

2. 在 `publish` 方法中，找到关键词过滤代码块之后，添加 AI 审核调用：

```java
// 在关键词过滤之后添加这一行
aiModerationService.moderatePost(postPublishDTO.getTitle(), postPublishDTO.getContent());
```

完整的 `publish` 方法审核流程应该是：
1. 参数校验（标题和内容非空）
2. 关键词过滤（Trie 树，快速，第一道防线）
3. AI 语义审核（大模型，智能，第二道防线）
4. 构建 Post 对象并入库

### 4.2 修改 WebMvcConfiguration —— 排除 AI 测试接口的认证

**文件路径**：`jsyl-common/src/main/java/com/jsyl/common/config/WebMvcConfiguration.java`

在用户端拦截器的 `excludePathPatterns` 中添加：

```java
.excludePathPatterns("/ai/**")
```

这样 AI 测试接口不需要登录就能访问，方便开发调试。生产环境应移除此排除规则。

## 五、注意事项

1. **降级策略**：AI 审核服务不可用时（网络超时、API 限流、解析异常），应降级跳过 AI 审核，只依赖关键词过滤，不影响用户正常发帖。这已在 `AiModerationServiceImpl` 中通过 try-catch 实现。

2. **demo key 限制**：LangChain4j 官方的 `demo` key 有配额限制，仅限 `gpt-4o-mini` 模型，仅用于开发测试。

3. **不要修改的文件**：`ContentModerationAi` 接口不需要写实现类，LangChain4j 的 `@AiService` 注解会在 Spring 启动时自动扫描并生成代理实现。

4. **Spring Boot 版本要求**：LangChain4j Spring Boot 集成需要 Spring Boot 3.2+，当前项目版本 3.2.5 满足要求。

## 六、验证步骤

1. 启动项目，观察日志中是否有 LangChain4j 相关的 Bean 注册信息
2. 访问 `GET http://localhost:8080/ai/chat?message=你好`，验证 AI 基础对话是否通
3. 访问 `POST http://localhost:8080/ai/moderate?title=正常标题&content=这是一篇正常的校园生活分享`，验证审核通过
4. 访问 `POST http://localhost:8080/ai/moderate?title=广告&content=加微信拉群日赚500`，验证审核拒绝
5. 通过发帖接口 `POST /jsyl/home/post/publish` 发布正常帖子，验证完整流程
