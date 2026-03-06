# 数据验证工具类 - ValidationUtil

## 类简介

`ValidationUtil` 是用于验证数据格式的工具类，提供用户名、密码、手机号等常用字段的正则表达式验证。

## 核心功能

1. **用户名验证** - 验证用户名格式（字母数字，2-20位）
2. **密码验证** - 验证密码格式（字母数字特殊字符，6-20位）
3. **手机号验证** - 验证中国手机号格式（1开头，11位）

## 正则表达式定义

```java
public class ValidationUtil {

    // 用户名：字母数字，2-20位
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]{2,20}$";

    // 密码：字母数字和常见特殊字符，6-20位
    private static final String PASSWORD_REGEX = "^[a-zA-Z0-9@$!%*?&]{6,20}$";

    // 手机号：中国大陆手机号，1开头，第二位3-9，共11位
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    // 编译成正则 Pattern 对象（提高性能）
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
}
```

## 验证方法详解

### 1. 用户名验证
```java
/**
 * 验证用户名格式
 * 规则：只能包含字母和数字，长度2-20位
 *
 * @param username 用户名
 * @return true-格式正确，false-格式错误
 */
public static boolean isValidUsername(String username) {
    if (username == null) {
        return false;
    }
    return USERNAME_PATTERN.matcher(username).matches();
}
```

**验证示例：**
| 输入 | 结果 | 说明 |
|------|------|------|
| `admin` | ✅ true | 纯字母，5位 |
| `user123` | ✅ true | 字母数字混合，6位 |
| `a` | ❌ false | 长度不足2位 |
| `user@name` | ❌ false | 包含特殊字符 |
| `用户名中文` | ❌ false | 包含中文 |

### 2. 密码验证
```java
/**
 * 验证密码格式
 * 规则：可包含字母、数字和常见特殊字符(@$!%*?&)，长度6-20位
 *
 * @param password 密码
 * @return true-格式正确，false-格式错误
 */
public static boolean isValidPassword(String password) {
    if (password == null) {
        return false;
    }
    return PASSWORD_PATTERN.matcher(password).matches();
}
```

**验证示例：**
| 输入 | 结果 | 说明 |
|------|------|------|
| `123456` | ✅ true | 纯数字，6位 |
| `password` | ✅ true | 纯字母，8位 |
| `Pass@123` | ✅ true | 字母数字特殊字符混合 |
| `12345` | ❌ false | 长度不足6位 |
| `123456789012345678901` | ❌ false | 超过20位 |
| `pass&word` | ❌ false | 包含不支持的特殊字符(&) |

### 3. 手机号验证
```java
/**
 * 验证手机号格式
 * 规则：中国大陆手机号，以1开头，第二位3-9，共11位
 *
 * @param phone 手机号（可为空，为空返回true）
 * @return true-格式正确或空，false-格式错误
 */
public static boolean isValidPhone(String phone) {
    if (phone == null || phone.isEmpty()) {
        return true;  // 空手机号视为有效（可选字段）
    }
    return PHONE_PATTERN.matcher(phone).matches();
}
```

**验证示例：**
| 输入 | 结果 | 说明 |
|------|------|------|
| `13812345678` | ✅ true | 正确格式 |
| `15900000000` | ✅ true | 正确格式 |
| `12345678901` | ❌ false | 第一位不是1 |
| `1381234567` | ❌ false | 不足11位 |
| `138123456789` | ❌ false | 超过11位 |
| `19812345678` | ❌ false | 第二位不是3-9 |
| `""` (空字符串) | ✅ true | 空手机号视为有效 |

## 使用示例

### 1. 在 Controller 中验证
```java
/**
 * 用户注册
 */
@PostMapping("/register")
public Result<Void> register(@RequestBody UserRegisterDTO dto) {
    // 验证用户名
    if (!ValidationUtil.isValidUsername(dto.getUsername())) {
        return Result.error("用户名只能包含字母和数字，长度2-20位");
    }

    // 验证密码
    if (!ValidationUtil.isValidPassword(dto.getPassword())) {
        return Result.error("密码长度6-20位，可包含字母数字和@$!%*?&");
    }

    // 验证手机号
    if (!ValidationUtil.isValidPhone(dto.getPhone())) {
        return Result.error("请输入正确的手机号");
    }

    // 继续业务逻辑
    userService.register(dto);
    return Result.success();
}
```

### 2. 在 Service 中验证
```java
/**
 * 修改手机号
 */
public void updatePhone(Integer userId, String phone) {
    // 验证手机号格式
    if (!ValidationUtil.isValidPhone(phone)) {
        throw new ParameterValidationException("手机号格式不正确");
    }

    // 继续业务逻辑
    userMapper.updatePhone(userId, phone);
}
```

### 3. 在表单验证规则中使用
```java
/**
 * 定义验证规则
 */
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (!/^[a-zA-Z0-9]{2,20}$/.test(value)) {
                    callback(new Error('用户名只能包含字母和数字，长度2-20位'));
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (!/^[a-zA-Z0-9@$!%*?&]{6,20}$/.test(value)) {
                    callback(new Error('密码长度6-20位'));
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ],
    phone: [
        {
            validator: (rule, value, callback) => {
                if (value && !/^1[3-9]\d{9}$/.test(value)) {
                    callback(new Error('请输入正确的手机号'));
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ]
};
```

## 常见正则表达式扩展

如果需要更多验证，可以扩展此类：

```java
// 邮箱验证
private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

// URL验证
private static final String URL_REGEX = "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(/.*)?$";
private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

// 身份证号验证
private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";
private static final Pattern ID_CARD_PATTERN = Pattern.compile(ID_CARD_REGEX);

public static boolean isValidEmail(String email) {
    if (email == null) return false;
    return EMAIL_PATTERN.matcher(email).matches();
}

public static boolean isValidUrl(String url) {
    if (url == null) return false;
    return URL_PATTERN.matcher(url).matches();
}

public static boolean isValidIdCard(String idCard) {
    if (idCard == null) return false;
    return ID_CARD_PATTERN.matcher(idCard).matches();
}
```

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/ValidationUtil.java` | 验证工具类 |
| `jsyl-server/src/main/java/com/jsyl/controller/UserController.java` | 用户控制器（使用验证） |
