/**
 * 验证规则工具模块
 * 
 * @module utils/validate
 */

import {
  USERNAME_MIN_LENGTH,
  USERNAME_MAX_LENGTH,
  PASSWORD_MIN_LENGTH,
  PASSWORD_MAX_LENGTH,
  PHONE_REGEX,
  EMAIL_REGEX
} from '@/constants/config';

// ==================== 验证规则类型 ====================

/**
 * 验证结果接口
 */
export interface ValidationResult {
  /** 是否验证通过 */
  valid: boolean;
  /** 错误消息（验证失败时） */
  message?: string;
}

// ==================== 用户名验证 ====================

/**
 * 验证用户名
 * @param username 用户名
 * @returns 验证结果
 */
export function validateUsername(username: string): ValidationResult {
  if (!username || username.trim() === '') {
    return { valid: false, message: '请输入用户名' };
  }

  const trimmedUsername = username.trim();

  if (trimmedUsername.length < USERNAME_MIN_LENGTH) {
    return { 
      valid: false, 
      message: `用户名长度不能少于${USERNAME_MIN_LENGTH}个字符` 
    };
  }

  if (trimmedUsername.length > USERNAME_MAX_LENGTH) {
    return { 
      valid: false, 
      message: `用户名长度不能超过${USERNAME_MAX_LENGTH}个字符` 
    };
  }

  const usernameRegex = /^[a-zA-Z0-9_]+$/;
  if (!usernameRegex.test(trimmedUsername)) {
    return { 
      valid: false, 
      message: '用户名只能包含字母、数字和下划线' 
    };
  }

  return { valid: true };
}

// ==================== 邮箱验证 ====================

/**
 * 验证邮箱
 * @param email 邮箱地址
 * @returns 验证结果
 */
export function validateEmail(email: string): ValidationResult {
  if (!email || email.trim() === '') {
    return { valid: false, message: '请输入邮箱地址' };
  }

  const trimmedEmail = email.trim();

  if (!EMAIL_REGEX.test(trimmedEmail)) {
    return { valid: false, message: '请输入有效的邮箱地址' };
  }

  return { valid: true };
}

// ==================== 手机号验证 ====================

/**
 * 验证手机号（可选字段为可选时）
 * @param phone 手机号码
 * @returns 验证结果
 */
export function validatePhone(phone: string): ValidationResult {
  if (!phone || phone.trim() === '') {
    return { valid: true };
  }

  const trimmedPhone = phone.trim();

  if (!PHONE_REGEX.test(trimmedPhone)) {
    return { valid: false, message: '请输入有效的手机号码' };
  }

  return { valid: true };
}

// ==================== 密码验证 ====================

/**
 * 验证密码
 * @param password 密码
 * @returns 验证结果
 */
export function validatePassword(password: string): ValidationResult {
  if (!password) {
    return { valid: false, message: '请输入密码' };
  }

  if (password.length < PASSWORD_MIN_LENGTH) {
    return { 
      valid: false, 
      message: `密码长度不能少于${PASSWORD_MIN_LENGTH}个字符` 
    };
  }

  if (password.length > PASSWORD_MAX_LENGTH) {
    return { 
      valid: false, 
      message: `密码长度不能超过${PASSWORD_MAX_LENGTH}个字符` 
    };
  }

  return { valid: true };
}

// ==================== 确认密码验证 ====================

/**
 * 验证确认密码
 * @param password 密码
 * @param confirmPassword 确认密码
 * @returns 验证结果
 */
export function validateConfirmPassword(password: string, confirmPassword: string): ValidationResult {
  const passwordResult = validatePassword(password);
  if (!passwordResult.valid) {
    return passwordResult;
  }

  if (!confirmPassword) {
    return { valid: false, message: '请输入确认密码' };
  }

  if (password !== confirmPassword) {
    return { valid: false, message: '两次输入的密码不一致' };
  }

  return { valid: true };
}

// ==================== 注册表单验证 ====================

/**
 * 注册表单参数接口
 */
export interface RegisterFormData {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  phone?: string;
}

/**
 * 验证注册表单
 * @param formData 表单数据
 * @returns 验证结果
 */
export function validateRegisterForm(formData: RegisterFormData): ValidationResult {
  const usernameResult = validateUsername(formData.username);
  if (!usernameResult.valid) {
    return usernameResult;
  }

  const emailResult = validateEmail(formData.email);
  if (!emailResult.valid) {
    return emailResult;
  }

  if (formData.phone) {
    const phoneResult = validatePhone(formData.phone);
    if (!phoneResult.valid) {
      return phoneResult;
    }
  }

  const passwordResult = validatePassword(formData.password);
  if (!passwordResult.valid) {
    return passwordResult;
  }

  const confirmPasswordResult = validateConfirmPassword(formData.password, formData.confirmPassword);
  if (!confirmPasswordResult.valid) {
    return confirmPasswordResult;
  }

  return { valid: true };
}
