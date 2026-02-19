# API 接口文档

## 概述

本文档描述了前端项目中所有API接口的使用说明，包括请求参数、响应格式和使用示例。

## 基础信息

- **API基础路径**: `/jsyl`
- **响应格式**: JSON
- **认证方式**: JWT Token (Bearer Token)

## 认证模块 API

### 用户登录

**接口地址**: `POST /jsyl/common/login`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| account | string | 是 | 用户账号 |
| password | string | 是 | 用户密码 |

**请求示例**:

```typescript
import { login } from '@/api/auth';

const result = await login({
  account: 'testuser',
  password: 'password123'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: {
    id: number,
    account: string,
    token: string
  }
}
```

---

### 用户注册

**接口地址**: `POST /jsyl/common/register`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名 (3-20字符) |
| email | string | 是 | 电子邮箱 |
| password | string | 是 | 密码 (6-20字符) |
| phone | string | 否 | 手机号码 |

**请求示例**:

```typescript
import { register } from '@/api/auth';

const result = await register({
  username: 'testuser',
  email: 'test@example.com',
  password: 'password123',
  phone: '13800138000'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

### 用户登出

**接口地址**: `POST /jsyl/common/logout`

**请求参数**: 无

**请求示例**:

```typescript
import { logout } from '@/api/auth';

const result = await logout();
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

## 用户模块 API

### 获取用户信息

**接口地址**: `GET /jsyl/common/userInfo`

**请求参数**: 无

**请求示例**:

```typescript
import { getUserInfo } from '@/api/user';

const result = await getUserInfo();
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: {
    id?: number,
    username?: string,
    email?: string,
    phone?: string,
    avatar?: string,
    createdAt?: string
  }
}
```

---

### 更新用户信息

**接口地址**: `POST /jsyl/common/updateUserInfo`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 否 | 用户名 |
| email | string | 否 | 电子邮箱 |
| phone | string | 否 | 手机号码 |
| avatar | string | 否 | 头像URL |

**请求示例**:

```typescript
import { updateUserInfo } from '@/api/user';

const result = await updateUserInfo({
  username: 'newname',
  email: 'new@example.com'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

### 修改密码

**接口地址**: `POST /jsyl/common/changePassword`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 原密码 |
| newPassword | string | 是 | 新密码 |

**请求示例**:

```typescript
import { changePassword } from '@/api/user';

const result = await changePassword({
  oldPassword: 'oldpass123',
  newPassword: 'newpass456'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

### 绑定手机

**接口地址**: `POST /jsyl/common/bindPhone`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号码 |
| code | string | 是 | 验证码 |

**请求示例**:

```typescript
import { bindPhone } from '@/api/user';

const result = await bindPhone({
  phone: '13800138000',
  code: '123456'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

### 绑定邮箱

**接口地址**: `POST /jsyl/common/bindEmail`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |
| code | string | 是 | 验证码 |

**请求示例**:

```typescript
import { bindEmail } from '@/api/user';

const result = await bindEmail({
  email: 'test@example.com',
  code: '123456'
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: null
}
```

---

## 订单模块 API

### 获取订单分页列表

**接口地址**: `GET /jsyl/home/orderCenter/page`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | number | 是 | 当前页码 (从1开始) |
| pageSize | number | 是 | 每页记录数 |
| orderNo | string | 否 | 订单号 (模糊搜索) |
| typeId | number | 否 | 订单类型ID (0=全部) |

**请求示例**:

```typescript
import { getOrderPage } from '@/api/order';

const result = await getOrderPage({
  page: 1,
  pageSize: 10,
  orderNo: '',
  typeId: 0
});
```

**响应数据**:

```typescript
{
  code: number,
  msg: string,
  data: {
    total: number,
    records: Array<{
      orderNo: string,
      userId: number,
      orderAmount: number,
      orderStatus: number,
      typeId: number,
      createTime: number[] | string | undefined
    }>
  }
}
```

---

## 通用响应格式

所有API接口统一返回以下格式：

```typescript
interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data: T;
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 常量定义

### 订单状态

| 值 | 常量名 | 说明 |
|----|--------|------|
| 1 | PENDING_PAYMENT | 待付款 |
| 2 | PENDING_SHIPMENT | 待发货 |
| 3 | PENDING_RECEIPT | 待收货 |
| 4 | COMPLETED | 已完成 |
| 5 | CANCELLED | 已取消 |

### 订单类型

| 值 | 常量名 | 说明 |
|----|--------|------|
| 1 | DELIVERY | 送货 |
| 2 | PICKUP | 取货 |
| 3 | PRINT | 打印 |
| 4 | SNACKS | 零食杂物 |
| 5 | TAKEOUT | 外卖 |
| 6 | EXPRESS | 快递 |

---

## 使用示例

### 完整的注册流程

```typescript
import { ref } from 'vue';
import { register } from '@/api/auth';
import { validateUsername, validateEmail, validatePassword } from '@/utils/validate';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
});

const handleRegister = async () => {
  try {
    await register({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password,
      phone: registerForm.value.phone
    });

    ElMessage.success('注册成功！请登录');
    router.push('/login');
  } catch (error) {
    ElMessage.error('注册失败，请重试');
  }
};
```

---

## 错误处理

所有API调用都应该包含错误处理逻辑：

```typescript
try {
  const result = await someApiFunction(params);
  if (result.code === 200) {
    // 处理成功
  } else {
    // 处理业务错误
    ElMessage.error(result.msg);
  }
} catch (error) {
  // 处理网络错误或其他异常
  if (error.response) {
    // 服务器返回错误
    ElMessage.error(error.response.data?.msg || '请求失败');
  } else if (error.request) {
    // 网络错误
    ElMessage.error('网络异常，请检查网络连接');
  } else {
    // 其他错误
    ElMessage.error('系统异常，请重试');
  }
}
```
