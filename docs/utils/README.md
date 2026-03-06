# 工具类文档目录

本文档包含校园服务平台所有工具类的详细说明。

## 文档列表

| 序号 | 文档名称 | 说明 |
|------|----------|------|
| 01 | [01_JwtUtil.md](./01_JwtUtil.md) | JWT令牌生成与解析工具 |
| 02 | [02_AliOssUtil.md](./02_AliOssUtil.md) | 阿里云OSS文件上传工具 |
| 03 | [03_HtmlSanitizerUtil.md](./03_HtmlSanitizerUtil.md) | HTML内容净化工具（XSS防护） |
| 04 | [04_HttpClientUtil.md](./04_HttpClientUtil.md) | HTTP客户端工具（GET/POST请求） |
| 05 | [05_ValidationUtil.md](./05_ValidationUtil.md) | 数据格式验证工具（用户名、密码、手机号） |
| 06 | [06_WeChatPayUtil.md](./06_WeChatPayUtil.md) | 微信支付工具（JSAPI支付、退款） |
| 07 | [07_SensitiveWordFilter.md](./07_SensitiveWordFilter.md) | 敏感词过滤器（DFA算法） |
| 08 | [08_RecommendationEngine.md](./08_RecommendationEngine.md) | 推荐引擎（热门度计算、相似度计算） |
| 09 | [09_SensitiveFilterResult.md](./09_SensitiveFilterResult.md) | 敏感词过滤结果封装类 |

## 工具类概览

### 安全认证类
| 工具类 | 功能 | 位置 |
|--------|------|------|
| JwtUtil | JWT令牌生成与解析 | jsyl-common |

### 文件处理类
| 工具类 | 功能 | 位置 |
|--------|------|------|
| AliOssUtil | 阿里云OSS文件上传 | jsyl-common |

### 数据验证类
| 工具类 | 功能 | 位置 |
|--------|------|------|
| ValidationUtil | 数据格式验证 | jsyl-common |
| HtmlSanitizerUtil | HTML内容净化 | jsyl-common |

### 网络请求类
| 工具类 | 功能 | 位置 |
|--------|------|------|
| HttpClientUtil | HTTP请求工具 | jsyl-common |
| WeChatPayUtil | 微信支付 | jsyl-common |

### 业务算法类
| 工具类 | 功能 | 位置 |
|--------|------|------|
| SensitiveWordFilter | 敏感词过滤 | jsyl-server |
| SensitiveFilterResult | 过滤结果封装 | jsyl-server |
| RecommendationEngine | 推荐算法 | jsyl-server |

## 快速导航

### 认证授权
- [JWT工具类](./01_JwtUtil.md) - 用户登录令牌管理

### 文件上传
- [阿里云OSS](./02_AliOssUtil.md) - 图片/文件存储

### 数据安全
- [HTML净化](./03_HtmlSanitizerUtil.md) - XSS攻击防护
- [敏感词过滤](./07_SensitiveWordFilter.md) - 内容安全过滤
- [验证工具](./05_ValidationUtil.md) - 数据格式校验

### 第三方集成
- [微信支付](./06_WeChatPayUtil.md) - 小程序支付
- [HTTP客户端](./04_HttpClientUtil.md) - 第三方API调用

### 智能推荐
- [推荐引擎](./08_RecommendationEngine.md) - 热门计算、相似度算法
