<template>
  <div class="register-container">
    <div class="register-content">
      <el-card class="register-card" shadow="hover">
        <template #header>
          <div class="register-header">
            <div class="register-title">校园服务平台</div>
            <div class="register-subtitle">账号注册</div>
          </div>
        </template>
        <el-form
          ref="registerFormRef"
          class="register-form"
          :model="registerForm"
          :rules="registerRules"
          @submit.prevent="handleRegister"
          autocomplete="off"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              clearable
              autocomplete="username"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入电子邮箱"
              clearable
              autocomplete="email"
              prefix-icon="Message"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              placeholder="请输入密码"
              type="password"
              clearable
              autocomplete="new-password"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              placeholder="请确认密码"
              type="password"
              clearable
              autocomplete="new-password"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号码（可选）"
              clearable
              autocomplete="tel"
              prefix-icon="Phone"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="register-button"
              @click="handleRegister"
              :loading="isLoading"
            >
              注册
            </el-button>
          </el-form-item>
          <el-form-item class="login-link-item">
            <div class="login-link">
              已有账号？<router-link to="/login">立即登录</router-link>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
      <div class="register-footer">
        <p>© 2026 校园服务平台 - 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

import { register, type RegisterParams } from '@/api/auth';
import { validateUsername, validateEmail, validatePassword, validatePhone } from '@/utils/validate';
import { logger } from '@/utils/logger';

const router = useRouter();
const registerFormRef = ref<FormInstance>();
const isLoading = ref(false);

const registerForm = ref<RegisterParams>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
});

const registerRules = reactive<FormRules<RegisterParams>>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }
        const result = validateUsername(value);
        if (result.valid) {
          callback();
        } else {
          callback(new Error(result.message));
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }
        const result = validateEmail(value);
        if (result.valid) {
          callback();
        } else {
          callback(new Error(result.message));
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }
        const result = validatePassword(value);
        if (result.valid) {
          callback();
        } else {
          callback(new Error(result.message));
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }
        const result = validatePhone(value);
        if (result.valid) {
          callback();
        } else {
          callback(new Error(result.message));
        }
      },
      trigger: 'blur'
    }
  ]
});

const handleRegister = async (): Promise<void> => {
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
  } catch {
    return;
  }

  try {
    isLoading.value = true;
    logger.info('开始注册', { username: registerForm.value.username });

    const { confirmPassword, ...registerData } = registerForm.value;
    await register(registerData);

    ElMessage.success('注册成功！请登录');
    logger.info('注册成功', { username: registerForm.value.username });

    router.push('/login');
  } catch (error: any) {
    logger.error('注册失败', error);
    handleRegisterError(error);
  } finally {
    isLoading.value = false;
  }
};

const handleRegisterError = (error: any): void => {
  if (error.response) {
    const errMsg = error.response.data?.msg || '注册失败，请检查输入信息！';
    ElMessage.error(`注册失败：${errMsg}`);
  } else if (error.request) {
    ElMessage.error('注册失败：网络异常，请检查网络连接！');
  } else {
    ElMessage.error(`注册失败：${error.message || '系统异常，请重试！'}`);
  }
};
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: var(--spacing-xl);
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
    background-size: 30px 30px;
    animation: float 20s linear infinite;
  }
}

.register-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  position: relative;
  z-index: 1;
}

.register-card {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-heavy);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all var(--transition-normal);
  overflow: hidden;

  &:hover {
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }
}

.register-header {
  text-align: center;
  padding: var(--spacing-2xl) var(--spacing-xl);
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--primary-active) 100%
  );
  color: #fff;
  margin: -1px -1px 0;
}

.register-title {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
  letter-spacing: 1px;
}

.register-subtitle {
  font-size: var(--font-size-sm);
  opacity: 0.9;
  font-weight: 400;
}

.register-form {
  padding: var(--spacing-2xl);
}

:deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

:deep(.el-input) {
  --el-input-bg-color: rgba(255, 255, 255, 0.9);
  --el-input-border-radius: var(--border-radius-lg);
  --el-input-height: 44px;
  --el-input-text-color: var(--text-primary);

  .el-input__wrapper {
    border-radius: var(--border-radius-lg);
    border: 1px solid var(--border-light);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
    transition: all var(--transition-fast);

    &:hover {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }

    &.is-focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15);
    }
  }

  .el-input__prefix {
    color: var(--text-tertiary);
  }

  .el-input__inner {
    font-size: var(--font-size-base);
    padding: 0 var(--spacing-lg);
  }
}

.register-button {
  width: 100%;
  height: 46px;
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-base);
  font-weight: 600;
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--primary-active) 100%
  );
  border: none;
  transition: all var(--transition-fast);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

  &:hover:not(.is-loading) {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }

  &:active:not(.is-loading) {
    transform: translateY(0);
  }
}

.login-link-item {
  margin-bottom: 0;
}

.login-link {
  width: 100%;
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);

  a {
    color: var(--primary-color);
    text-decoration: none;
    font-weight: 500;
    transition: color var(--transition-fast);

    &:hover {
      color: var(--primary-active);
      text-decoration: underline;
    }
  }
}

.register-footer {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: var(--font-size-xs);
  padding: var(--spacing-lg);
  background: rgba(0, 0, 0, 0.1);
  border-radius: var(--border-radius-lg);
  backdrop-filter: blur(5px);
}

@keyframes float {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  100% {
    transform: translate(-25%, -25%) rotate(360deg);
  }
}

@media (max-width: 768px) {
  .register-container {
    padding: var(--spacing-lg);
  }

  .register-card {
    max-width: 100%;
  }

  .register-header {
    padding: var(--spacing-xl);
  }

  .register-form {
    padding: var(--spacing-xl);
  }

  .register-title {
    font-size: var(--font-size-xl);
  }

  :deep(.el-input) {
    --el-input-height: 42px;
  }

  .register-button {
    height: 42px;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: var(--spacing-md);
  }

  .register-header {
    padding: var(--spacing-lg);
  }

  .register-form {
    padding: var(--spacing-lg);
  }
}
</style>
