
import {
  USERNAME_MIN_LENGTH,
  USERNAME_MAX_LENGTH,
  PASSWORD_MIN_LENGTH,
  PASSWORD_MAX_LENGTH,
  PHONE_REGEX
} from '@/constants/config';


export interface ValidationResult {
  /** 是否验证通过 */
  valid: boolean;
  /** 错误消息（验证失败时） */
  message?: string;
}


export function validateUsername(username: string): ValidationResult {
  if (!username || username.trim() === '') {
    return { valid: false, message: '请输入用户名' };
  }

  const trimmedUsername = username.trim();

  if (trimmedUsername.length < 2) {
    return {
      valid: false,
      message: '用户名长度不能少于2个字符'
    };
  }

  if (trimmedUsername.length > 20) {
    return {
      valid: false,
      message: '用户名长度不能超过20个字符'
    };
  }

  const usernameRegex = /^[a-zA-Z0-9]+$/;
  if (!usernameRegex.test(trimmedUsername)) {
    return {
      valid: false,
      message: '用户名只能包含字母和数字'
    };
  }

  return { valid: true };
}


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


export interface RegisterFormData {
  username: string;
  password: string;
  confirmPassword: string;
  phone?: string;
}

export function validateRegisterForm(formData: RegisterFormData): ValidationResult {
  const usernameResult = validateUsername(formData.username);
  if (!usernameResult.valid) {
    return usernameResult;
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
