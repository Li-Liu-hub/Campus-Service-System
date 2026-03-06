# 敏感词过滤结果类 - SensitiveFilterResult

## 类简介

`SensitiveFilterResult` 是敏感词过滤操作的返回值封装类，用于携带过滤后的文本和是否包含敏感词的标记。

## 类结构

```java
/**
 * 敏感词过滤结果
 * 封装过滤后的文本和敏感词检测结果
 */
public class SensitiveFilterResult {
    // 过滤后的文本（敏感词已被替换为星号）
    private final String filteredText;

    // 是否包含敏感词
    private final boolean hasSensitive;

    // 构造函数
    public SensitiveFilterResult(String filteredText, boolean hasSensitive) {
        this.filteredText = filteredText;
        this.hasSensitive = hasSensitive;
    }

    // Getter 方法
    public String getFilteredText() {
        return filteredText;
    }

    public boolean hasSensitive() {
        return hasSensitive;
    }
}
```

## 核心功能

1. **返回过滤后文本** - 将敏感词替换为星号后的文本
2. **标记敏感词存在** - 标识原始文本是否包含敏感词

## 使用示例

### 1. Service 中使用
```java
/**
 * 发布评论
 */
public void publishComment(Comment comment) {
    // 执行过滤
    SensitiveFilterResult result = sensitiveWordFilter.filter(comment.getContent());

    // 获取过滤后的文本
    String filteredContent = result.getFilteredText();
    comment.setContent(filteredContent);

    // 检查是否包含敏感词
    if (result.hasSensitive()) {
        // 可以选择：拒绝发布 / 标记审核 / 记录日志
        log.warn("评论包含敏感词，待审核: userId={}", comment.getUserId());
    }

    // 保存评论
    commentMapper.insert(comment);
}
```

### 2. 分离获取结果
```java
// 方式1：获取过滤后文本
String text = filterResult.getFilteredText();

// 方式2：检查是否包含敏感词
boolean hasSensitive = filterResult.hasSensitive();

// 方式3：链式调用
if (filterResult.hasSensitive()) {
    // 处理包含敏感词的情况
}
```

## 过滤示例

### 示例1：包含敏感词
```java
// 输入
String input = "This is a fuck word";

// 过滤
SensitiveFilterResult result = sensitiveWordFilter.filter(input);

// 输出
result.getFilteredText()  // "This is a **** word"
result.hasSensitive()     // true
```

### 示例2：不包含敏感词
```java
// 输入
String input = "This is a normal text";

// 过滤
SensitiveFilterResult result = sensitiveWordFilter.filter(input);

// 输出
result.getFilteredText()  // "This is a normal text"
result.hasSensitive()     // false
```

### 示例3：空输入
```java
// 输入
String input = null;

// 过滤
SensitiveFilterResult result = sensitiveWordFilter.filter(input);

// 输出
result.getFilteredText()  // null
result.hasSensitive()     // false
```

## 在 SensitiveWordFilter 中的使用

```java
public SensitiveFilterResult filter(String text) {
    if (text == null || text.isEmpty()) {
        // 空输入返回空结果
        return new SensitiveFilterResult(text, false);
    }

    char[] chars = text.toCharArray();
    boolean matched = false;
    int length = chars.length;

    // ... 过滤逻辑 ...

    // 返回结果
    return new SensitiveFilterResult(new String(chars), matched);
}

public boolean containsSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
        return false;
    }
    // 委托给 filter 方法
    SensitiveFilterResult result = filter(text);
    return result.hasSensitive();
}

public String maskSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
        return text;
    }
    // 委托给 filter 方法
    SensitiveFilterResult result = filter(text);
    return result.getFilteredText();
}
```

## 设计模式

该类使用了**值对象（Value Object）模式**：

1. **不可变性** - 使用 `final` 字段，创建后不可修改
2. **值相等** - 比较的是值而不是引用
3. **简洁性** - 只包含数据和简单的 getter 方法

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-server/src/main/java/com/jsyl/utils/SensitiveFilterResult.java` | 过滤结果类 |
| `jsyl-server/src/main/java/com/jsyl/utils/SensitiveWordFilter.java` | 敏感词过滤器 |
