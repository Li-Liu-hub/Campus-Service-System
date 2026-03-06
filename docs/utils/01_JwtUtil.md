# JWT 工具类 - JwtUtil

## 类简介

`JwtUtil` 是校园服务平台的核心安全工具类，负责生成和解析 JWT（JSON Web Token）令牌。基于 JJWT 库实现，支持管理员和普通用户两套独立的密钥和过期时间配置。

## 核心功能

1. **生成 JWT Token** - 为用户和管理员生成身份令牌
2. **解析 JWT Token** - 验证并解析令牌中的载荷信息
3. **配置校验** - 启动时校验密钥和过期时间配置

## 类结构

```java
@Component
public class JwtUtil {
    // 密钥配置（从yml注入）
    private String adminSecretKey;    // 管理员密钥
    private long adminTtl;           // 管理员过期时间
    private String userSecretKey;     // 普通用户密钥
    private long userTtl;            // 普通用户过期时间
}
```

## 核心方法详解

### 1. 生成管理员 JWT
```java
/**
 * 生成管理员 JWT Token
 * @param claims 自定义载荷信息（用户ID、用户名、角色等）
 * @return JWT Token 字符串
 */
public String createAdminJWT(Map<String, Object> claims) {
    return createJWT(adminSecretKey, adminTtl, claims);
}
```

### 2. 生成用户 JWT
```java
/**
 * 生成普通用户 JWT Token
 * @param claims 自定义载荷信息
 * @return JWT Token 字符串
 */
public String createUserJWT(Map<String, Object> claims) {
    return createJWT(userSecretKey, userTtl, claims);
}
```

### 3. 解析管理员 JWT
```java
/**
 * 解析管理员 JWT Token
 * @param token JWT Token 字符串
 * @return 载荷Claims对象
 */
public Claims parseAdminJWT(String token) {
    return parseJWT(adminSecretKey, token);
}
```

### 4. 解析用户 JWT
```java
/**
 * 解析普通用户 JWT Token
 * @param token JWT Token 字符串
 * @return 载荷Claims对象
 */
public Claims parseUserJWT(String token) {
    return parseJWT(userSecretKey, token);
}
```

### 5. 核心生成方法
```java
/**
 * JWT 生成核心方法
 * @param secretKey 签名密钥
 * @param ttlMillis 过期时间（毫秒）
 * @param claims 自定义载荷
 * @return JWT Token 字符串
 */
private String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
    // 参数校验
    validateSecretKey(secretKey, "");
    validateTtl(ttlMillis, "");
    if (claims == null) {
        throw new IllegalArgumentException("JWT载荷（claims）不能为空！");
    }

    // 1. 指定签名算法（HS256）
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 2. 计算过期时间
    long expMillis = System.currentTimeMillis() + ttlMillis;
    Date exp = new Date(expMillis);

    // 3. 构建JWT
    JwtBuilder builder = Jwts.builder()
            .setClaims(claims)  // 设置载荷
            // 使用字节数组密钥，适配0.9.1版本
            .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
            .setExpiration(exp); // 设置过期时间

    return builder.compact(); // 生成token
}
```

### 6. 核心解析方法
```java
/**
 * JWT 解析核心方法
 * @param secretKey 签名密钥
 * @param token JWT Token 字符串
 * @return 载荷Claims对象
 */
private Claims parseJWT(String secretKey, String token) {
    // 参数校验
    validateSecretKey(secretKey, "");
    if (token == null || token.trim().isEmpty()) {
        throw new IllegalArgumentException("JWT令牌（token）不能为空！");
    }

    try {
        // 使用parser()方法解析（0.9.1版本）
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    } catch (Exception e) {
        throw new RuntimeException("JWT解析失败：" + e.getMessage(), e);
    }
}
```

## 配置说明

在 `application.yml` 中配置：

```yaml
jsyl:
  jwt:
    # 管理员JWT配置
    admin-secret-key: your-admin-secret-key
    admin-ttl: 7200000  # 2小时（毫秒）

    # 普通用户JWT配置
    user-secret-key: your-user-secret-key
    user-ttl: 86400000  # 24小时（毫秒）
```

## 使用示例

### 生成用户登录Token
```java
// 在用户登录成功后生成Token
Map<String, Object> claims = new HashMap<>();
claims.put(JwtClaimsConstant.USER_ID, user.getId());
claims.put(JwtClaimsConstant.USERNAME, user.getNickname());
claims.put(JwtClaimsConstant.ROLE, user.getRole());

String token = jwtUtil.createUserJWT(claims);
```

### 验证用户Token
```java
// 在拦截器中验证Token
try {
    Claims claims = jwtUtil.parseUserJWT(token);
    Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
    // 将用户ID存入线程上下文，供后续使用
    BaseContext.setCurrentId(userId);
} catch (Exception e) {
    throw new UserNotLoginException();
}
```

## JWT 载荷标准声明

系统定义了以下常量用于载荷键名：

```java
// JwtClaimsConstant.java
public class JwtClaimsConstant {
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String ROLE = "role";
}
```

## 安全机制

1. **密钥分离** - 管理员和用户使用不同密钥
2. **过期时间分离** - 管理员Token有效期更短
3. **启动校验** - `@PostConstruct` 启动时检查配置
4. **参数校验** - 每次操作前验证密钥和Token有效性

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/JwtUtil.java` | JWT工具类 |
| `jsyl-common/src/main/java/com/jsyl/properties/JwtProperties.java` | JWT配置属性 |
| `jsyl-common/src/main/java/com/jsyl/constant/JwtClaimsConstant.java` | JWT载荷常量 |
| `jsyl-server/src/main/java/com/jsyl/interceptor/JwtTokenUserInterceptor.java` | 用户Token拦截器 |
| `jsyl-server/src/main/java/com/jsyl/interceptor/JwtTokenAdminInterceptor.java` | 管理员Token拦截器 |
