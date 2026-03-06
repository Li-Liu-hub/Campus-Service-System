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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import { login, type LoginParams, type LoginApiParams } from "@/api/auth";

const TOKEN_KEY = "token";
const ACCOUNT_KEY = "login_account";
const USER_HOME_PATH = "/user/home";
const ADMIN_HOME_PATH = "/admin/dashboard";

const router = useRouter();
const loginForm = ref<LoginParams>({
  account: "",
  password: "",
  remember: false,
});
const isLoading = ref(false);

const handleLogin = async (): Promise<void> => {
  const account = loginForm.value.account.trim();
  const password = loginForm.value.password.trim();

  if (!account) {
    ElMessage.warning("请输入账号！");
    return;
  }
  if (!password) {
    ElMessage.warning("请输入密码！");
    return;
  }

  try {
    isLoading.value = true;
    const res = await login({ account, password });

    if (res?.code !== 200) {
      ElMessage.error(res?.msg || "登录失败");
      return;
    }

    const token = res?.data?.token || (res as any)?.token;
    if (!token) {
      ElMessage.error("登录失败：未获取到认证令牌");
      return;
    }

    const userData = res?.data;
    const userRole = userData?.role || 1;

    localStorage.setItem(TOKEN_KEY, token);
    if (userData) {
      localStorage.setItem("userInfo", JSON.stringify({
        id: userData.id,
        account: userData.account,
        nickname: userData.nickname || account,
        role: userRole,
      }));
    }

    if (loginForm.value.remember) {
      localStorage.setItem(ACCOUNT_KEY, account);
    } else {
      localStorage.removeItem(ACCOUNT_KEY);
    }

    ElMessage.success("登录成功！");
    router.push(userRole === 4 ? ADMIN_HOME_PATH : USER_HOME_PATH);
  } catch (error: any) {
    if (error.code !== undefined && error.msg) {
      ElMessage.error(error.msg);
    } else if (error.response) {
      ElMessage.error(error.response.data?.msg || "账号或密码错误");
    } else if (error.request) {
      ElMessage.error("网络异常，请检查网络连接");
    } else {
      ElMessage.error(error.message || "系统异常");
    }
  } finally {
    isLoading.value = false;
  }
};

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
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;

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
    background-size: 50px 50px;
    animation: float 20s linear infinite;
  }

  &::after {
    content: "";
    position: absolute;
    top: 10%;
    right: 10%;
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    animation: pulse 4s ease-in-out infinite;
  }
}

.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.35);
  }
}

/* 登录头部 */
.login-header {
  text-align: center;
  padding: 40px 32px 24px;
  background: linear-gradient(180deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
}

.login-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 15px;
  color: #6b7280;
  font-weight: 500;
}

/* 登录表单 */
.login-form {
  padding: 0 40px 40px;
}

.login-form-item {
  margin-bottom: 24px;
}

:deep(.el-input) {
  --el-input-bg-color: #f9fafb;
  --el-input-border-radius: 12px;
  --el-input-height: 52px;
  --el-input-text-color: #1f2937;

  .el-input__wrapper {
    border-radius: 12px;
    border: 2px solid transparent;
    box-shadow: 0 0 0 1px #e5e7eb inset;
    background-color: #f9fafb;
    transition: all 0.2s;
    padding: 4px 16px;

    &:hover {
      background-color: #f3f4f6;
      box-shadow: 0 0 0 1px #d1d5db inset;
    }

    &.is-focus {
      background-color: #fff;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2) !important;
      border-color: #667eea;
    }
  }

  .el-input__prefix {
    color: #9ca3af;
    font-size: 18px;
  }

  .el-input__inner {
    font-size: 15px;
    padding: 0 12px;
  }
}

/* 记住账号选项 */
.remember-item {
  margin-bottom: 28px;
}

.remember-checkbox {
  --el-checkbox-font-size: 14px;
  --el-checkbox-text-color: #6b7280;

  &:hover {
    --el-checkbox-text-color: #667eea;
  }
}

/* 注册链接 */
.register-link-item {
  margin-bottom: 0;
  margin-top: 20px;
}

.register-link {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #6b7280;

  a {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
    transition: all 0.2s;

    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }
  }
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);

  &:hover:not(.is-loading) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
    background: linear-gradient(135deg, #7c8ff5 0%, #8a5cb8 100%);
  }

  &:active:not(.is-loading) {
    transform: translateY(0);
  }
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  padding: 16px;
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

/* 加载按钮动画 */
:deep(.el-button--primary.is-loading) {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    width: 100%;
    max-width: 380px;
  }

  .login-form {
    padding: 0 24px 32px;
  }

  .login-header {
    padding: 32px 24px 20px;
  }

  .login-title {
    font-size: 26px;
  }
}
</style>
