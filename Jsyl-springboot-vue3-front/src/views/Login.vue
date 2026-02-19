<template>
  <div class="login-container">
    <div class="login-content">
      <el-card class="login-card" shadow="hover">
        <template #header>
          <div class="login-header">
            <div class="login-title">校园服务平台</div>
            <div class="login-subtitle">账号登录</div>
          </div>
        </template>
        <div class="role-selector">
          <el-radio-group v-model="loginRole" size="large">
            <el-radio-button value="user">
              <el-icon><User /></el-icon>
              用户
            </el-radio-button>
            <el-radio-button value="admin">
              <el-icon><Setting /></el-icon>
              管理员
            </el-radio-button>
          </el-radio-group>
        </div>
        <el-form
          class="login-form"
          :model="loginForm"
          @submit.prevent="handleLogin"
          autocomplete="off"
        >
          <el-form-item class="login-form-item">
            <el-input
              v-model="loginForm.account"
              placeholder="请输入用户名"
              clearable
              autocomplete="username"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="login-form-item">
            <el-input
              v-model="loginForm.password"
              placeholder="请输入密码"
              type="password"
              clearable
              autocomplete="current-password"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="login-form-item remember-item">
            <el-checkbox v-model="loginForm.remember" class="remember-checkbox">
              记住账号
            </el-checkbox>
          </el-form-item>
          <el-form-item class="login-form-item">
            <el-button
              type="primary"
              class="login-button"
              @click="handleLogin"
              :loading="isLoading"
            >
              登录
            </el-button>
          </el-form-item>
          <el-form-item class="login-form-item register-link-item">
            <div class="register-link">
              还没有账号？<router-link to="/register">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
      <div class="login-footer">
        <p>© 2026 校园服务平台 - 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 登录页面组件
 *
 * 功能说明：
 * - 用户登录认证
 * - 记住账号功能
 * - 登录成功后跳转首页
 */

import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock, Setting } from "@element-plus/icons-vue";

import { login, type LoginParams, type LoginApiParams } from "@/api/auth";

// ==================== 常量定义 ====================

const TOKEN_KEY = "token";
const ACCOUNT_KEY = "login_account";
const USER_HOME_PATH = "/user/home";
const ADMIN_HOME_PATH = "/admin/dashboard";

// ==================== 响应式数据 ====================

const router = useRouter();

const loginForm = ref<LoginParams>({
  account: "",
  password: "",
  remember: false,
});

const loginRole = ref<"user" | "admin">("user");
const isLoading = ref(false);

// ==================== 登录处理 ====================

/**
 * 处理登录
 */
const handleLogin = async (): Promise<void> => {
  const account = loginForm.value.account.trim();
  const password = loginForm.value.password.trim();

  if (!validateForm(account, password)) return;

  try {
    isLoading.value = true;

    const loginParams: LoginApiParams = { account, password };
    console.log("开始登录，参数:", loginParams);

    const res = await login(loginParams);
    console.log("登录完整响应:", res);
    console.log("响应类型:", typeof res);
    console.log("响应 code:", res?.code);
    console.log("响应 msg:", res?.msg);
    console.log("响应 data:", res?.data);

    let token: string | undefined;

    if (res?.code === 200 && res?.data?.token) {
      token = res.data.token;
    } else if (res?.data?.token) {
      token = res.data.token;
    } else if ((res as any)?.token) {
      token = (res as any).token;
    }

    if (token) {
      console.log("获取到token:", token);
      const userData = res?.data;
      handleLoginSuccess(token, account, userData);
    } else {
      console.error("未找到token，完整响应:", JSON.stringify(res, null, 2));
      ElMessage.error(res?.msg || "登录失败：未获取到认证令牌，请重试！");
    }
  } catch (error: any) {
    console.error("登录异常:", error);
    handleLoginError(error);
  } finally {
    isLoading.value = false;
  }
};

/**
 * 验证表单
 * @param account 账号
 * @param password 密码
 * @returns 是否验证通过
 */
const validateForm = (account: string, password: string): boolean => {
  if (!account) {
    ElMessage.warning("请输入账号！");
    return false;
  }
  if (!password) {
    ElMessage.warning("请输入密码！");
    return false;
  }
  return true;
};

/**
 * 处理登录成功
 * @param token 认证令牌
 * @param account 账号
 */
const handleLoginSuccess = (
  token: string,
  account: string,
  userData: any
): void => {
  console.log("处理登录成功...");
  console.log("Token:", token);
  console.log("Account:", account);
  console.log("用户数据:", userData);
  console.log("当前角色:", loginRole.value);

  localStorage.setItem(TOKEN_KEY, token);

  if (userData) {
    const userInfo = {
      id: userData.id,
      account: userData.account,
      nickname: userData.nickname || account,
    };
    localStorage.setItem("userInfo", JSON.stringify(userInfo));
    console.log("已保存用户信息到localStorage:", userInfo);
  }

  if (loginForm.value.remember) {
    localStorage.setItem(ACCOUNT_KEY, account);
  } else {
    localStorage.removeItem(ACCOUNT_KEY);
  }

  ElMessage.success("登录成功！");

  const targetPath =
    loginRole.value === "admin" ? ADMIN_HOME_PATH : USER_HOME_PATH;
  console.log("准备跳转到:", targetPath);

  router
    .push(targetPath)
    .then(() => {
      console.log("路由跳转成功");
    })
    .catch((err) => {
      console.error("路由跳转失败:", err);
      ElMessage.error("页面跳转失败，请手动刷新");
    });
};

/**
 * 处理登录错误
 * @param error 错误对象
 */
const handleLoginError = (error: any): void => {
  console.error("登录失败详情：", error);

  if (error.response) {
    const errMsg = error.response.data?.msg || "账号或密码错误，请检查！";
    ElMessage.error(`登录失败：${errMsg}`);
  } else if (error.request) {
    ElMessage.error("登录失败：网络异常，请检查网络连接！");
  } else {
    ElMessage.error(`登录失败：${error.message || "系统异常，请重试！"}`);
  }
};

// ==================== 生命周期 ====================

onMounted(() => {
  const savedAccount = localStorage.getItem(ACCOUNT_KEY);
  if (savedAccount) {
    loginForm.value = {
      account: savedAccount,
      password: "",
      remember: true,
    };
  }
});
</script>

<style lang="scss" scoped>
/* 登录容器 - 渐变背景 */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;

  /* 背景装饰 */
  &::before {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
      circle,
      rgba(255, 255, 255, 0.1) 1px,
      transparent 1px
    );
    background-size: 30px 30px;
    animation: float 20s linear infinite;
  }
}

/* 登录内容区域 */
.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

/* 登录卡片 - 固定宽度 */
.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

/* 登录头部 */
.login-header {
  text-align: center;
  padding: 24px 20px;
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
  color: #fff;
  margin: -1px -1px 0;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 400;
}

.role-selector {
  display: flex;
  justify-content: center;
  padding: 16px 24px 0;
}

.role-selector :deep(.el-radio-group) {
  width: 100%;
}

.role-selector :deep(.el-radio-button) {
  flex: 1;
  text-align: center;

  .el-radio-button__inner {
    width: 100%;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    font-weight: 500;
  }
}

/* 登录表单 */
.login-form {
  padding: 24px;
}

.login-form-item {
  margin-bottom: 16px;
}

/* 表单输入框 - 固定高度 */
:deep(.el-input) {
  --el-input-bg-color: rgba(255, 255, 255, 0.9);
  --el-input-border-radius: 8px;
  --el-input-height: 48px;
  --el-input-text-color: #303133;

  .el-input__wrapper {
    border-radius: 8px;
    border: 1px solid #ebeef5;
    box-shadow: none;
    transition: border-color 0.2s;

    &:hover {
      border-color: #409eff;
    }

    &.is-focus {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }

  .el-input__prefix {
    color: #909399;
  }

  .el-input__inner {
    font-size: 16px;
    padding: 0 16px;
  }
}

/* 记住账号选项 */
.remember-item {
  margin-bottom: 20px;
}

.remember-checkbox {
  --el-checkbox-font-size: 14px;
  --el-checkbox-text-color: #606266;

  &:hover {
    --el-checkbox-text-color: #409eff;
  }
}

/* 注册链接 */
.register-link-item {
  margin-bottom: 0;
}

.register-link {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #606266;

  a {
    color: #409eff;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s;

    &:hover {
      color: #3a8ee6;
      text-decoration: underline;
    }
  }
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
  border: none;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

  &:hover:not(.is-loading) {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }

  &:active:not(.is-loading) {
    transform: translateY(0);
  }
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(5px);
}

/* 动画效果 */
@keyframes float {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  100% {
    transform: translate(-25%, -25%) rotate(360deg);
  }
}

/* 加载按钮动画 */
:deep(.el-button--primary.is-loading) {
  background: linear-gradient(135deg, #3a8ee6 0%, #409eff 100%);
}
</style>
