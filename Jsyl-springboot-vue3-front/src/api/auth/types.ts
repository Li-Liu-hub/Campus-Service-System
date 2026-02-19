/**
 * 认证模块类型定义
 * 
 * @module api/auth/types
 */

// ==================== 登录相关类型 ====================

/**
 * 登录参数接口
 */
export interface LoginParams {
  /** 用户账号 */
  account: string;
  /** 用户密码 */
  password: string;
  /** 是否记住账号（前端使用，不传递给后端） */
  remember?: boolean;
}

/**
 * 登录API参数接口（不包含remember字段）
 */
export type LoginApiParams = Omit<LoginParams, 'remember'>;

/**
 * 登录响应数据接口
 */
export interface LoginResponse {
  /** 用户ID */
  id: number;
  /** 用户账号 */
  account: string;
  /** JWT认证令牌 */
  token: string;
}

// ==================== 注册相关类型 ====================

/**
 * 注册参数接口
 */
export interface RegisterParams {
  /** 用户名 */
  username: string;
  /** 电子邮箱 */
  email: string;
  /** 密码 */
  password: string;
  /** 确认密码 */
  confirmPassword: string;
  /** 手机号码（可选） */
  phone?: string;
}

/**
 * 注册API参数接口（不包含confirmPassword字段）
 */
export interface RegisterApiParams {
  /** 用户名 */
  username: string;
  /** 电子邮箱 */
  email: string;
  /** 密码 */
  password: string;
  /** 手机号码（可选） */
  phone?: string;
}

/**
 * 注册响应数据接口
 */
export interface RegisterResponse {
  /** 用户ID */
  id: number;
  /** 用户名 */
  username: string;
  /** 电子邮箱 */
  email: string;
  /** 手机号码（可选） */
  phone?: string;
  /** JWT认证令牌 */
  token?: string;
  /** 创建时间 */
  createdAt?: string;
}

// ==================== 登出相关类型 ====================

/**
 * 登出响应数据接口
 */
export interface LogoutResponse {
  /** 是否登出成功 */
  success: boolean;
  /** 消息 */
  message?: string;
}
